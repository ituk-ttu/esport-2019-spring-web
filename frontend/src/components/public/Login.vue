<template lang="pug">
  .container.center-content
      steam-login
</template>

<script>
  import SteamLogin from './SteamLogin.vue';
  export default {
    name: 'Login',
    data () {
      return {};
    },
    methods: {},
    mounted: function () {},
    components: {
      'steam-login': SteamLogin
    }
  };
</script>
