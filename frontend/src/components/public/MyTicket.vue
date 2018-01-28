<template lang="pug">
  blockquote.my-ticket
    p.lead {{ $t('buy.name') }}:
      span.text-primary  {{ticket.name}}
    p: small.text-default {{ $t('buy.invoiceNumber') }}:
      span.text-primary  {{getInvoiceNumber(ticket.id)}}
    p: small.text-default {{ $t('buy.ticket') }}:
      span.text-primary  {{ $t('tickets.names["' + ticket.type.name + '"]') }}
    p: small.text-default {{ticket.name}}: &nbsp
      ticket-status(:status="ticket.status")
    p: small.text-default {{ $t('tickets.boughtOnDate') }}:
      span.text-primary  {{ticket.dateCreated | moment("Do MMMM HH:mm") }}
    p(v-if="canCancelTicket(ticket)")
      button.btn.btn-danger(v-on:click="cancelTicket()") {{ $t('tickets.cancel') }}
    p {{ $t('buy.total') }}:
      span.text-primary  {{ticket.type.cost}} €
    //confirm-dialog(:show.sync="shouldShowCancelDialog", :onConfirm="cancelTicket")
      | {{ $t('tickets.cancel.confirm', ticket) }}:

</template>

<script>
  import ConfirmDialog from './ConfirmDialog';
  import TicketStatus from './TicketStatus';

  export default {
    components: { ConfirmDialog, TicketStatus },
    name: 'MyTicket',
    props: ['ticket'],
    data () {
      return {
        shouldShowCancelDialog: false
      };
    },
    methods: {
      getInvoiceNumber: function () {
        let ticketId = this.ticket.id.toString();
        return '2018-359027-' + '000'.substring(0, 3 - ticketId.length) + ticketId;
      },
      canCancelTicket: function () {
        return this.$ticket.ownerCanCancel(this.ticket);
      },
      showCancelDialog: function () {
        this.shouldShowCancelDialog = true;
      },
      cancelTicket: function () {
        this.$ticket.cancel(this.ticket).then(res => { this.ticket.status = 'CANCELED'; });
      },
      cancelDialogId: function () {
        return 'cancelDialog.' + this.ticket.id;
      }
    }
  };
</script>
