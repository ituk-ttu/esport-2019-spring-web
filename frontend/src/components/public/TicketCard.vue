<template lang="pug">
    .card.full-height-card
      .card-header
        .card-header-title
          .container
            h1.title {{ ticket.name }}
            h2.subtitle {{ offering.name }}
      .card-content
        p
          span.has-text-burgundy.has-text-weight-bold {{ $t('tickets.status') }}: &nbsp;
          ticket-status(:status="ticket.status")
        p
          span.has-text-burgundy.has-text-weight-bold {{ $t('tickets.boughtOnDate') }}: &nbsp;
          span {{ticket.dateCreated | moment("Do MMMM HH:mm") }}

</template>

<script>
  import ConfirmDialog from './ConfirmDialog';
  import TicketStatus from './TicketStatus';

  export default {
    components: { TicketStatus },
    name: 'TicketCard',
    props: ['ticket', 'offering', 'type '],
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
