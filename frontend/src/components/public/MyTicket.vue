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
    p: small.text-default
      | {{ $t('tickets.members.title') }}
      ul.members
        li.member(v-for="member in ticket.members")
          form.form-inline(v-show="member.id === existingMemberOpen")
            .form-group
              input.form-control(v-model="idCode")
              button.btn.btn-sm.btn-primary(v-on:click="saveMember(member)")
                i.glyphicon.glyphicon-ok
              button.btn.btn-sm.btn-primary(v-on:click="closeOpen()")
                i.glyphicon.glyphicon-remove
          div(v-show="member.id !== existingMemberOpen")
            span.idCode {{ member.idCode }}
            button.btn.btn-sm.btn-primary(v-on:click="openMember(member)")
              i.glyphicon.glyphicon-pencil
            button.btn.btn-sm.btn-default(v-on:click="deleteMember(member)")
              i.glyphicon.glyphicon-trash
        li.member(v-show="canAddMember()")
          button.btn.btn-sm.btn-primary(v-on:click="openAddMember()")
            i.glyphicon.glyphicon-plus
            | &nbsp; {{ $t('tickets.members.add') }}
        li.member(v-show="addMemberOpen")
          form.form-inline
            .form-group
              input.form-control(v-model="idCode")
              button.btn.btn-sm.btn-primary(v-on:click="addMember()")
                i.glyphicon.glyphicon-ok
              button.btn.btn-sm.btn-primary(v-on:click="closeOpen()")
                i.glyphicon.glyphicon-remove
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
        shouldShowCancelDialog: false,
        addMemberOpen: false,
        addMemberPolling: false,
        existingMemberOpen: null,
        idCode: null
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
      },
      canAddMember: function () {
        return this.ticket.members.length < this.ticket.type.teamSize && !this.addMemberPolling && !this.addMemberOpen;
      },
      openAddMember: function () {
        this.idCode = '';
        this.addMemberOpen = true;
        this.existingMemberOpen = null;
      },
      addMember: function () {
        const self = this;
        self.addMemberPolling = true;
        self.addMemberOpen = false;
        return this.$ticket.storeMember(this.ticket, { idCode: this.idCode }).then(res => {
          self.addMemberPolling = false;
          self.$forceUpdate();
        });
      },
      openMember: function (member) {
        this.idCode = member.idCode;
        this.addMemberOpen = false;
        this.existingMemberOpen = member.id;
      },
      saveMember: function (member) {
        const self = this;
        self.existingMemberOpen = null;
        member.idCode = this.idCode;
        return this.$ticket.storeMember(this.ticket, member).then(res => { self.$forceUpdate(); });
      },
      closeOpen: function () {
        this.addMemberOpen = false;
        this.existingMemberOpen = null;
      },
      deleteMember: function (member) {
        const self = this;
        return this.$ticket.removeMember(this.ticket, member).then(res => { self.$forceUpdate(); });
      }
    }
  };
</script>
<style>
  .member {
    margin: 5px 0;
  }
  .member .btn {
    margin-left: 5px;
  }
  .member .idCode {
    margin: 0 10px;
  }
</style>
