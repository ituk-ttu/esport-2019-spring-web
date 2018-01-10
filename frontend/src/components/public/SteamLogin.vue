<template lang="pug">
  button.btn-sm.btn.btn-default(:disabled="disabled" v-on:click="startLogin")
    i.fa.fa-steam
    |  {{ $t('buy.connectWithSteam') }}
</template>

<script>
  export default {
    name: 'SteamLogin',
    methods: {
      startLogin: function () {
        const self = this;
        const newWindow = window.open(self.loginUrl, '_blank', 'width=800, height=600');
        window.addEventListener('message', event => {
          if (newWindow !== event.source) {
            return;
          }
          const url = event.data.url;
          const params = {};
          for (let entry of new URL(url).searchParams) {
            params[entry[0]] = entry[1];
          }
          params['receivingUrl'] = url;
          self.$http.get(self.$config.apiBase + '/api/steam/verify', {
            params: params
          }).then(result => {
            console.log('self.onLoggedIn()');
            return self.$http.get(self.$config.apiBase + '/api/ticketTypes');
          });
        });
      }
    },
    mounted: function () {
      const self = this;
      self.$http.get(self.$config.apiBase + '/api/steam/loginLink', {
        params: {
          returnTo: self.$config.steamLoginReturnTo
        }
      }).then(function (response) {
        self.disabled = false;
        self.loginUrl = response.body;
      });
    },
    data: () => ({
      disabled: true,
      loginUrl: null
    })
  };
</script>
