<template lang="pug">
  div
    .container(v-if="!$parent.isAdmin()")
      h1.text-center(v-t="'tickets.tickets'")
      blockquote.my-ticket(v-for="ticket in tickets")
        p.lead {{ $t('buy.name') }}:
          span.text-primary  {{ticket.name}}
        p: small.text-default {{ $t('buy.invoiceNumber') }}:
          span.text-primary  {{getInvoiceNumber(ticket.id)}}
        p: small.text-default {{ $t('buy.ticket') }}:
          span.text-primary  {{ $t('tickets.names["' + ticket.type.name + '"]') }}
        p: small.text-default {{ $t('tickets.status') }}:&nbsp
          | {{ $t('tickets.statuses["' + ticket.status + '"]') }}
        p: small.text-default {{ $t('tickets.boughtOnDate') }}:
          span.text-primary  {{ticket.dateCreated | moment("Do MMMM HH:mm") }}
        p(v-if="canConfirmTicket(ticket)")
          button.btn.btn-success(v-on:click="confirmTicket(ticket)") {{ $t('tickets.confirm') }}
        p(v-if="canCancelTicket(ticket)")
          button.btn.btn-danger(v-on:click="cancelTicket(ticket)") {{ $t('tickets.cancel') }}
        p {{ $t('buy.total') }}:
          span.text-primary  {{ticket.type.cost}} €
    .container(v-else)
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
        table-column(show="status" label="Status")
          template(slot-scope="row")
            span.label(:class="getStatusClass(row.status)")
              small {{ $t('tickets.statuses["' + row.status + '"]') }}
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
  export default {
    name: 'MyTickets',
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
      getStatusClass: function (status) {
        switch (status) {
          case 'IN_WAITING_LIST':
            return 'label-warning';
          case 'AWAITING_PAYMENT':
            return 'label-info';
          case 'PAID':
            return 'label-success';
          case 'CANCELED':
            return 'label-danger';
          default:
            return 'label-primary';
        }
      },
      canConfirmTicket: function (ticket) {
        return this.$ticket.canConfirm(ticket);
      },
      canCancelTicket: function (ticket) {
        return this.$ticket.canCancel(ticket);
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
      self.$ticket.getTickets().then(tickets => { self.tickets = tickets; });
    },
    computed: {
      // a computed getter
      stats: function () {
        let stats = [];
        let count = {};
        for (let i = 0; i < this.tickets.length; i++) {
          if (!count.hasOwnProperty(this.tickets[i].type.id)) {
            count[this.tickets[i].type.id] = {
              total: 0,
              byStatus: {
                IN_WAITING_LIST: 0,
                AWAITING_PAYMENT: 0,
                PAID: 0,
                CANCELED: 0
              }
            };
          }
          count[this.tickets[i].type.id].total += 1;
          count[this.tickets[i].type.id].byStatus[this.tickets[i].status] += 1;
        }
        for (let property in count) {
          if (count.hasOwnProperty(property)) {
            stats.push({
              typeId: parseInt(property),
              name: this.$parent.getTicketTypeNameById(property),
              total: count[property].total,
              byStatus: count[property].byStatus
            });
          }
        }
        return stats;
      }
    }
  };
</script>
<style>
  .table-component__filter__clear, .table-component__table__caption, .stats-table-wrapper .table-component__filter {
    display: none !important;
  }
</style>
