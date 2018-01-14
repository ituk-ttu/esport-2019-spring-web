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
    }
  };
</script>
