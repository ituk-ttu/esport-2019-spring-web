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
          steam-login(disabled)
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
        self.$ticket.buy(self.ticketDetails, self.ticket.id).then(ticket => {
          self.bought = true;
        }).catch(() => {
          self.$notify({
            title: self.$t('buy.fail.title'),
            text: self.$t('buy.fail.text')
          });
        }).finally(() => {
          self.sending = false;
        });
      }
    },
    mounted: function () {
      const self = this;
      self.$ticket.getType(this.$route.params.ticketId).then(type => {
        self.ticket = type;
      });
    },
    components: {
      'steam-login': SteamLogin
    }
  };
</script>
