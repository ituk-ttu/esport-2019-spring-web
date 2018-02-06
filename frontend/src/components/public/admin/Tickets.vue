<template lang="pug">
  .container
    .form-group.stats-table-wrapper
      span(v-if="statsExpanded")
        button.btn.btn-primary(v-on:click="statsExpanded = false") Close stats
        table-component(:data="stats" sort-by="id" sort-order="desc" table-class="table" show-filter="false"
                        filter-input-class="form-control" show-caption="false" filter-no-results="¯\\_(ツ)_/¯")
          table-column(show="name" label="Ticket Type")
            template(slot-scope="row")
              strong {{ row.name }}
          table-column(show="byStatus.CANCELED" label="Canceled" data-type="numeric" cell-class="text-danger")
          table-column(show="byStatus.IN_WAITING_LIST" label="In Waiting List" data-type="numeric" cell-class="text-warning")
          table-column(show="byStatus.AWAITING_PAYMENT" label="Awaiting Payment" data-type="numeric" cell-class="text-info")
          table-column(show="byStatus.PAID" label="Paid" data-type="numeric" cell-class="text-success")
          table-column(show="total" label="total" data-type="numeric")
            template(slot-scope="row")
              strong {{ row.total }}
      span(v-if="!statsExpanded && tickets.length > 0")
        button.btn.btn-primary(v-on:click="statsExpanded = true") Open stats
    table-component(:data="tickets" sort-by="dateCreated" sort-order="desc" table-class="table"
                    filter-input-class="form-control" show-caption="false" filter-no-results="¯\\_(ツ)_/¯")
      table-column(show="id" label="ID" data-type="numeric")
      table-column(show="name" label="Name")
        template(slot-scope="row")
          strong {{ row.name }}
      table-column(show="type.name" label="Type")
      table-column(show="ownerEmail" label="Owner Email")
      table-column(show="type.cost" label="Cost" data-type="numeric")
      table-column(label="Members entered" data-type="numeric")
        template(slot-scope="row")
          | {{ row.members.length }}/{{ row.type.teamSize }}
      table-column(show="status" label="Status")
        template(slot-scope="row")
          ticket-status(:status="row.status")
      table-column(show="createdAt" label="Bought on")
        template(slot-scope="row"): small {{row.dateCreated | moment("Do MMMM HH:mm") }}
      table-column(label="", :sortable="false", :filterable="false")
        template(slot-scope="row")
          span(v-if="canConfirmTicket(row)")
            button.btn.btn-success.btn-xs(v-on:click="confirmTicket(row)"): small {{ $t('tickets.confirm') }}
      table-column(label="", :sortable="false", :filterable="false")
        template(slot-scope="row")
          span(v-if="canCancelTicket(row)")
            button.btn.btn-danger.btn-xs(v-on:click="cancelTicket(row)"): small {{ $t('tickets.cancel') }}
</template>

<script>
  import TicketStatus from '../TicketStatus.vue';

  export default {
    components: { TicketStatus },
    name: 'AdminTickets',
    data () {
      return {
        statsExpanded: false,
        tickets: [],
        columns: [
          {
            title: 'id'
          }
        ]
      };
    },
    methods: {
      getInvoiceNumber: function (id) {
        return '2018-359027-' + '000'.substring(0, 3 - id.toString().length) + id.toString();
      },
      canConfirmTicket: function (ticket) {
        return this.$ticket.adminCanConfirm(ticket);
      },
      canCancelTicket: function (ticket) {
        return this.$ticket.adminCanCancel(ticket);
      },
      confirmTicket: function (ticket) {
        this.$ticket.confirm(ticket).then(res => { ticket.status = 'PAID'; });
      },
      cancelTicket: function (ticket) {
        this.$ticket.cancel(ticket).then(res => { ticket.status = 'CANCELED'; });
      }
    },
    created: function () {
      if (!this.$auth.isLoggedIn()) {
        this.$router.push({ name: 'Login' });
      }
    },
    mounted: function () {
      const self = this;
      self.$ticket.getAllTickets().then(tickets => { self.tickets = tickets; });
    },
    computed: {
      // a computed getter
      stats: function () {
        let stats = {};
        this.tickets.forEach(ticket => {
          const type = ticket.type;
          if (!stats.hasOwnProperty(type.id)) {
            stats[type.id] = {
              total: 0,
              typeId: type.id,
              name: type.name,
              byStatus: {
                IN_WAITING_LIST: 0,
                AWAITING_PAYMENT: 0,
                PAID: 0,
                CANCELED: 0
              }
            };
          }
          if (ticket.status !== 'CANCELED') {
            stats[type.id].total += 1;
          }
          stats[type.id].byStatus[ticket.status] += 1;
        });
        console.log(stats);
        return Object.values(stats);
      }
    }
  };
</script>
<style>
  .table-component__filter__clear, .table-component__table__caption, .stats-table-wrapper .table-component__filter {
    display: none !important;
  }
</style>
