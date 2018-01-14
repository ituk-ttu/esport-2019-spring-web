<template lang="pug">
  .center-content
    i.fa.fa-gear.fa-spin.fa-2x
</template>

<script>
  export default {
    name: 'TicketLogin',
    data () {
      return {};
    },
    mounted: function () {
      const self = this;
      self.$auth.performEmailLinkLogin(this.$route.params.loginKey).then(() => {
        self.$router.push({ name: 'MyTickets' });
      });
    }
  };
</script>
