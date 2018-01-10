<template lang="pug">
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
        span.label(:class="getStatusClass(ticket.status)")  {{ $t('tickets.statuses["' + ticket.status + '"]') }}
      p: small.text-default {{ $t('tickets.boughtOnDate') }}:
        span.text-primary  {{ticket.dateCreated | moment("Do MMMM HH:mm") }}
      p(v-if="canConfirmTicket(ticket)")
        button.btn.btn-success(v-on:click="confirmTicket(ticket)") {{ $t('tickets.confirm') }}
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
        tickets: null
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
        return this.$auth.isAdmin() && ticket.status === 'IN_WAITING_LIST';
      },
      canCancelTicket: function (ticket) {
        return (this.$auth.isAdmin() && ticket.status !== 'CANCELED') ||
               ['IN_WAITING_LIST', 'PAID'].includes(ticket.status);
      },
      confirmTicket: function (ticket) {
        this.$http.post(this.$config.apiBase + '/api/ticket/' + ticket.id + '/confirm')
          .then(res => { ticket.status = 'PAID'; });
      },
      cancelTicket: function (ticket) {
        this.$http.post(this.$config.apiBase + '/api/ticket/' + ticket.id + '/cancel')
          .then(res => { ticket.status = 'CANCELED'; });
      }
    },
    mounted: function () {
      const self = this;
      this.$http.get(this.$config.apiBase + '/api/tickets').then(result => {
        self.tickets = result.body;
      });
    }
  };
</script>
