<template lang="pug">
  .container.center-content
      steam-login(:onSuccess="onSuccess")
</template>

<script>
  import SteamLogin from './SteamLogin.vue';
  export default {
    name: 'Login',
    data () {
      return {};
    },
    methods: {
      onSuccess: function () {
        this.$router.push({ name: 'MyTickets' });
      }
    },
    created: function () {
      if (this.$auth.isLoggedIn()) {
        this.$router.push({ name: 'MyTickets' });
      }
    },
    components: {
      'steam-login': SteamLogin
    }
  };
</script>
