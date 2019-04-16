<template lang="pug">
  .modal.is-active
    .modal-background
    .modal-card
      .modal-card-head
        .modal-card-title
          h2.title(v-t="'tickets.seatSelect'")
          h3.subtitle {{ ticket.name }} ({{ offering.name }})
        button.delete(@click="onClose")
      .modal-card-body
        .has-text-centered(v-if="availableSeats == null"): i.fa.fa-2x.fa-cog.fa-spin
        .notification.is-warning(v-else-if="ticket.status !== 'PAID'") {{ $t('tickets.notPaidSeating') }}
        seating(v-else :ticket="ticket" :ticketType="type" :availableSeats="availableSeats")
</template>

<script>
  import ConfirmDialog from './ConfirmDialog';
  import TicketStatus from './TicketStatus';
  import Seating from '../presentation/seating/Seating';

  export default {
    components: {ConfirmDialog, TicketStatus, Seating},
    name: 'SeatingModal',
    props: ['ticket', 'offering', 'type'],
    data() {
      return {
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
    methods: {
      onClose: function() {
        this.$emit('close');
      }
    },
    mounted: function () {
      const self = this;
      this.$ticket.getAvailableSeats(this.ticket.id).then(seats => {
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
