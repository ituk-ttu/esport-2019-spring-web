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
          form.form-inline(v-if="member.id === existingMemberOpen")
            .form-group
              label {{ $t('tickets.members.idCode') }}
                input.form-control(v-model="newMember.idCode")
            .form-group
              label {{ $t('tickets.members.igName') }}
                input.form-control(v-model="newMember.igName")
            .form-group
              button.btn.btn-sm.btn-primary(v-on:click="saveMember()")
                i.glyphicon.glyphicon-ok
              button.btn.btn-sm.btn-primary(v-on:click="closeOpen()")
                i.glyphicon.glyphicon-remove
          div(v-else)
            span.memberInfo
              span.idCode {{ member.idCode }}
              span.igName(v-if="member.igName") &nbsp; ({{ member.igName }})
            button.btn.btn-sm.btn-primary(v-on:click="openMember(member)")
              i.glyphicon.glyphicon-pencil
            button.btn.btn-sm.btn-default(v-on:click="deleteMember(member)")
              i.glyphicon.glyphicon-trash
        li.member(v-if="canAddMember()")
          button.btn.btn-sm.btn-primary(v-on:click="openAddMember()")
            i.glyphicon.glyphicon-plus
            | &nbsp; {{ $t('tickets.members.add') }}
        li.member(v-if="addMemberOpen")
          form.form-inline
            .form-group
              input.form-control(v-model="newMember.idCode", :placeholder="$t('tickets.members.idCode')")
            .form-group
              input.form-control(v-model="newMember.igName", :placeholder="$t('tickets.members.igName')")
            .form-group
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
        newMember: {
          idCode: null,
          igName: null
        }
      };
    },
    methods: {
      getInvoiceNumber: function () {
        let ticketId = this.ticket.id.toString();
        return '2019-359027-' + '000'.substring(0, 3 - ticketId.length) + ticketId;
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
        this.newMember = {
          idCode: '',
          igName: ''
        };
        this.addMemberOpen = true;
        this.existingMemberOpen = null;
      },
      addMember: function () {
        const self = this;
        self.addMemberPolling = true;
        self.addMemberOpen = false;
        return this.$ticket.storeMember(this.ticket, this.newMember).then(res => {
          self.addMemberPolling = false;
          self.$forceUpdate();
        });
      },
      openMember: function (member) {
        this.newMember = Object.assign({}, member);
        this.addMemberOpen = false;
        this.existingMemberOpen = member.id;
      },
      saveMember: function () {
        const self = this;
        self.existingMemberOpen = null;
        return this.$ticket.storeMember(this.ticket, this.newMember).then(res => { self.$forceUpdate(); });
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
  .member .btn, .member .form-control, .member .memberInfo {
    margin-right: 10px;
  }
</style>
