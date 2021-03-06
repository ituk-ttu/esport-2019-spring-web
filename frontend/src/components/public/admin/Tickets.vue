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
      table-column( label="Type")
        template(slot-scope="row")
          | {{ getOffering(row).name }}
      table-column( label="Owner Email")
        template(slot-scope="row")
          strong {{ getOwnerEmail(row) }}
      table-column(label="Cost" data-type="numeric")
        template(slot-scope="row")
          | {{ getOffering(row).cost }} €
      table-column(label="Members entered" data-type="numeric")
        template(slot-scope="row")
          | {{ row.members.length }}/{{ getType(getOffering(row).typeId).teamSize }}
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
      <!--table-column(label="", :sortable="false", :filterable="false")-->
        <!--template(slot-scope="row")-->
          <!--button.btn.btn-danger.btn-xs(v-on:click="sendEmail(row)"): small Send email-->
</template>

<script>
  import TicketStatus from '../TicketStatus.vue';

  export default {
    components: { TicketStatus },
    name: 'AdminTickets',
    data () {
      return {
        statsExpanded: false,
        ownerEmails: {},
        tickets: [],
        ticketTypes: [],
        offerings: [],
        columns: [
          {
            title: 'id'
          }
        ]
      };
    },
    methods: {
      getInvoiceNumber: function (id) {
        return '2019-359027-' + '000'.substring(0, 3 - id.toString().length) + id.toString();
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
      sendEmail: function (ticket) {
        this.$ticket.sendEmail(ticket).then(res => { });
      },
      cancelTicket: function (ticket) {
        this.$ticket.cancel(ticket).then(res => { ticket.status = 'CANCELED'; });
      },
      getOffering: function (ticket) {
        let currentOffering = 'Unknown';
        this.offerings.forEach(offering => {
          if (offering.id === ticket.offeringId) {
            currentOffering = offering;
          }
        });
        return currentOffering;
      },
      getType: function(typeId) {
        let currentTicketType = 'Unknown';
        this.ticketTypes.forEach(ticketType => {
          if (ticketType.id === typeId) {
            currentTicketType = ticketType;
          }
        });
        return currentTicketType;
      },
      getOwnerEmail: function (ticket) {
        const self = this;
        return self.ownerEmails[ticket.id];
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
      self.$ticket.getAllOfferings().then(offerings => { self.offerings = offerings; });
      self.$ticket.getAllTicketTypes().then(ticketTypes => { self.ticketTypes = ticketTypes; });
      self.$ticket.getAllOwnerEmails().then(ownerEmails => { self.ownerEmails = ownerEmails; });
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
