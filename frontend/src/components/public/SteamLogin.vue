<template lang="pug">
  div
    button.button.is-black.is-large(:disabled="disabled" v-on:click="startLogin")
      i.fa.fa-steam
      |  {{ $t('buy.connectWithSteam') }}
    .modal.is-active(v-if="isRegistering")
      .modal-background
      .modal-card
        form(v-on:submit.prevent="register()")
          .modal-card-head
            h2.title(v-t="'register.title'")
          .modal-card-body
            .field
              label.label(v-t="'register.name'")
              .control
                input.input(v-model="userDetails.name" required)
            .field
              label.label(v-t="'register.email'")
              .control
                input.input(v-model="userDetails.email" required)
          .modal-card-foot
            .field
              .control
                button.button.is-link(type="submit" v-t="'register.register'")
        button.modal-close.is-large(v-on:click="cancelRegistration")
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
            if (result.type === 'LOGIN') {
              self.$auth.logIn(result.user, result.token);
              self.onSuccess();
              return;
            }
            if (result.type === 'NEW_USER') {
              let {registrationToken, userDetails} = result;
              this.userDetails = userDetails;
              this.registrationToken = registrationToken;
              this.isRegistering = true;
              return;
            }
            throw new Error('Failed to log in');
          }).catch(e => {
            self.$notify({
              title: self.$t('login.failure.title'),
              text: self.$t('login.failure.text')
            });
          });
        });
      },
      register: function () {
        const self = this;
        self.$auth.register(self.registrationToken, self.userDetails).then(result => {
          self.$auth.logIn(result.user, result.token);
          self.onSuccess();
        }).catch(e => {
          self.$notify({
            title: self.$t('login.failure.title'),
            text: self.$t('login.failure.text')
          });
        }).finally(() => {
          self.isRegistering = false;
        });
      },
      cancelRegistration: function () {
        this.isRegistering = false;
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
      userDetails: null,
      registrationToken: null,
      isRegistering: false,
      loginUrl: null
    })
  };
</script>
