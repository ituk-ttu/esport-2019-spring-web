<template lang="pug">
  div
    .container
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
        p(v-if="canCancelTicket(ticket)")
          button.btn.btn-danger(v-on:click="cancelTicket(ticket)") {{ $t('tickets.cancel') }}
        p {{ $t('buy.total') }}:
          span.text-primary  {{ticket.type.cost}} €
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
      canCancelTicket: function (ticket) {
        return this.$ticket.canCancel(ticket);
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
      self.$ticket.getMyTickets().then(tickets => { self.tickets = tickets; });
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
