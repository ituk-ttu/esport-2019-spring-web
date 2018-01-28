<template lang="pug">
  .container
    h1.text-center(v-t="'tickets.tickets'")
    my-ticket(v-for="ticket in tickets", :key="ticket.id", :ticket="ticket")
</template>

<script>
  import MyTicket from './MyTicket';
  export default {
    components: {MyTicket},
    name: 'MyTickets',
    data () {
      return {
        tickets: []
      };
    },
    created: function () {
      if (!this.$auth.isLoggedIn()) {
        this.$router.push({ name: 'Login' });
      }
    },
    mounted: function () {
      const self = this;
      self.$ticket.getMyTickets().then(tickets => { self.tickets = tickets; });
    }
  };
</script>
<style>
  .table-component__filter__clear, .table-component__table__caption, .stats-table-wrapper .table-component__filter {
    display: none !important;
  }
</style>
