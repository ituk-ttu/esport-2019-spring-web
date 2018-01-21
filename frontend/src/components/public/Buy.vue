<template lang="pug">
  div
    .container(v-if="!bought")
      h1.text-center {{ $t('buy.buyTicket') }}
      h2.text-center {{ ticket.name }}
      form(v-on:submit.prevent="send()")
        .form-group
          label.control-label(v-t="ticket.teamSize <= 1 ? 'buy.name' : 'buy.teamName'")
          input.form-control.input-lg(v-model="ticketDetails.name" required)
        .form-group
          label.control-label(v-t="'buy.email'")
          input.form-control.input-lg(v-model="ticketDetails.ownerEmail" type="email" required)
        .form-group
          steam-login(:onSuccess="onSteamLogin" v-if="!isSteamLoggedIn()")
          label(v-else)
            input(type="checkbox" checked v-model="shouldConnectWithSteam")
            | {{ $t('buy.shouldConnectWithSteam', { username: getSteamUsername() }) }}
        blockquote.form-group
          p {{ $t('buy.numberOfPlayers') }}:
            span.text-primary  {{ ticket.teamSize }}
          p {{ $t('buy.pricePerPlayer') }}:
            span.text-primary  {{ ticket.cost / ticket.teamSize }}€
          p.lead {{ $t('buy.total') }}:
            span.text-primary  {{ ticket.cost }}€
        .form-group
          button.btn.btn-primary.btn-lg(type="submit" v-if="!sending")
            | {{ $t('buy.buyTicket') }}
          button.btn.btn-primary.btn-lg.disabled(type="submit" v-if="sending" disabled)
            i.fa.fa-cog.fa-spin
        .form-group
          p.text-muted(v-t="'buy.info'")
    .container(v-if="bought")
      h1.text-center {{ $t('buy.success.title') }}
      p.lead(v-t="'buy.checkMail'")
      p(v-t="'buy.paymentInfo'")
</template>

<script>
  import SteamLogin from './SteamLogin.vue';
  export default {
    name: 'Buy',
    data () {
      return {
        shouldConnectWithSteam: true,
        bought: false,
        sending: false,
        ticket: null,
        ticketDetails: {
          name: '',
          ownerEmail: ''
        }
      };
    },
    methods: {
      send: function () {
        const self = this;
        if (this.sending) {
          return;
        }
        self.sending = true;
        let ticketDetails = self.ticketDetails;
        if (self.isSteamLoggedIn() && self.shouldConnectWithSteam) {
          ticketDetails.ownerSteamId = self.getSteamId();
        }
        self.$ticket.buy(ticketDetails, self.ticket.id).then(ticket => {
          self.bought = true;
        }).catch(() => {
          self.$notify({
            title: self.$t('buy.fail.title'),
            text: self.$t('buy.fail.text')
          });
        }).finally(() => {
          self.sending = false;
        });
      },
      isSteamLoggedIn: function () {
        const self = this;
        return self.$auth.isLoggedIn() && self.$auth.getClaims().steam_user != null;
      },
      getSteamUsername: function () {
        const self = this;
        return self.$auth.getClaims().steam_user.name;
      },
      getSteamId: function () {
        const self = this;
        return self.$auth.getClaims().steam_user.id;
      },
      onSteamLogin: function () {
        this.$forceUpdate();
      }
    },
    mounted: function () {
      const self = this;
      self.$ticket.getType(parseInt(this.$route.params.ticketId)).then(type => {
        self.ticket = type;
      });
    },
    components: {
      'steam-login': SteamLogin
    }
  };
</script>
