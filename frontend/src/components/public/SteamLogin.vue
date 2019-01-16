<template lang="pug">
  button.button.is-black.is-large(:disabled="disabled" v-on:click="startLogin")
    i.fa.fa-steam
    |  {{ $t('buy.connectWithSteam') }}
</template>

<script>
  export default {
    name: 'SteamLogin',
    props: ['onSuccess'],
    methods: {
      startLogin: function () {
        const self = this;
        const newWindow = window.open(self.loginUrl, '_blank', 'width=800, height=600');
        window.addEventListener('message', event => {
          if (newWindow !== event.source) {
            return;
          }
          self.$auth.verifySteamLogin(event.data.url).then(result => {
            this.onSuccess();
          });
        });
      }
    },
    mounted: function () {
      const self = this;
      self.$auth.getSteamLoginLink(self.$config.steamLoginReturnTo).then(function (response) {
        self.disabled = false;
        self.loginUrl = response;
      });
    },
    data: () => ({
      disabled: true,
      loginUrl: null
    })
  };
</script>
