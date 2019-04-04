<template lang="pug">

  .container
    section.section
      router-link(:to="{name: 'MyTickets'}") < {{ $t('navbar.myTickets') }}
      h1.title.is-center(v-t="'tickets.seatSelect'")
      span(v-if="ticket == null || type == null") ...
      span(v-else-if="!type.assignedSeating") {{ $t('tickets.noAssignedSeating') }}
      span(v-else-if="ticket.status !== 'PAID'") {{ $t('tickets.notPaidSeating') }}
      seating(v-else :ticket="ticket" :ticketType="type" :availableSeats="availableSeats")

</template>

<script>
  import ConfirmDialog from './ConfirmDialog';
  import TicketStatus from './TicketStatus';
  import Seating from '../presentation/seating/Seating';

  export default {
    components: { ConfirmDialog, TicketStatus, Seating },
    name: 'MyTicket',
    props: ['ticketId'],
    data () {
      return {
        ticket: null,
        type: null,
        availableSeats: null,
        isError: false,
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
    mounted: function () {
      const self = this;
      const ticketId = parseInt(self.ticketId);
      Promise.all([this.$ticket.getUserTickets(self.$auth.getUser().id),
                   this.$ticket.getTypes(),
                   this.$ticket.getAvailableSeats(ticketId)])
             .then(([tickets, types, seats]) => {
               self.ticket = tickets.find(it => it.id === ticketId);
               self.type = types.find(it => it.id === self.ticket.typeId);
               self.availableSeats = seats;
             });
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
