<template lang="pug">
  .container
    section.section
      h1.title.is-center(v-t="'tickets.tickets'")
      .has-text-centered(v-if="tickets == null || offerings == null || types == null"): i.fa.fa-2x.fa-cog.fa-spin
      div(v-else)
        ticket-card(v-for="ticket in tickets" :key="ticket.id"
                    :ticket="ticket" :type="getType(ticket.typeId)" :offering="getOffering(ticket.offeringId)")
</template>

<script>
  import TicketCard from "./TicketCard";
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
