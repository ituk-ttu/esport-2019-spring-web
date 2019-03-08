<template lang="pug">
  .container
    section.section
      h1.title.is-center(v-t="'tickets.tickets'")
      .container
        .has-text-centered(v-if="isLoading"): i.fa.fa-2x.fa-cog.fa-spin
        .columns.is-multiline.is-centered(v-else)
          .column.is-one-quarter-fullhd.is-one-third-desktop.is-half-tablet.is-full-mobile(v-for="ticket in tickets"
                                                                                           :key="ticket.id"
                                                                                           v-if="!isEmpty")
            ticket-card(:ticket="ticket" :type="getType(ticket.typeId)" :offering="getOffering(ticket.offeringId)")
          .column.is-full(v-else)
            p.has-text-dark.is-size-3 Piletid puuduvad.
</template>

<script>
  import TicketCard from './TicketCard';
  export default {
    components: { TicketCard },
    name: 'MyTickets',
    data () {
      return {
        tickets: null,
        offerings: null,
        types: null
      };
    },
    created: function () {
      if (!this.$auth.isLoggedIn()) {
        this.$router.push({ name: 'Login' });
      }
    },
    computed: {
      isLoading: function () {
        return this.tickets === null || this.offerings === null || this.types === null;
      },
      isEmpty: function () {
        return !this.isLoading() && this.tickets.length === 0;
      }
    },
    methods: {
      getType: function (id) {
        return this.types.find(type => type.id === id);
      },
      getOffering: function (id) {
        return this.offerings.find(offering => offering.id === id);
      }
    },
    mounted: function () {
      const self = this;
      self.$ticket.getUserTickets(self.$auth.getUser().id).then(tickets => {
        self.tickets = tickets;
      });
      self.$ticket.getVisibleOfferings().then(offerings => {
        self.offerings = offerings; // TODO: Loading, error
      });
      self.$ticket.getTypes().then(types => {
        self.types = types; // TODO: Loading, error
      });
    }
  };
</script>
<style>
  .table-component__filter__clear, .table-component__table__caption, .stats-table-wrapper .table-component__filter {
    display: none !important;
  }
</style>
