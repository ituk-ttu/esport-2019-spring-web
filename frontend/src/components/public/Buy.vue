<template lang="pug">
  section.section
    .container(v-if="bought")
      h1.title.has-text-centered.has-text-primary.is-1(v-t="'buy.success.title'")
      h2.subtitle.has-text-centered(v-t="'buy.checkMail'")
      p.has-text-centered(v-t="'buy.paymentInfo'")
    steam-login(:onSuccess="onSteamLogin" v-else-if="!isLoggedIn()")
    .has-text-centered(v-else-if="offering == null || type == null"): i.fa.fa-2x.fa-cog.fa-spin
    .container(v-else)
      h1.title.has-text-primary(v-t="'buy.buyTicket'")
      h2.subtitle {{ offering.name }}
      form(v-on:submit.prevent="send()")
        .field
          label.label(v-t="type.teamSize <= 1 ? 'buy.name' : 'buy.teamName'")
          .control
            input.input(v-model="ticketDetails.name" required)
        blockquote.field
          p {{ $t('buy.numberOfPlayers') }}:
            span.text-primary  {{ type.teamSize }}
          p {{ $t('buy.pricePerPlayer') }}:
            span.text-primary  {{ offering.cost / type.teamSize }}€
          p.lead {{ $t('buy.total') }}:
            span.text-primary  {{ offering.cost }}€
        .field
          .control
            button.button.is-link(type="submit" v-if="!sending")
              | {{ $t('buy.buyTicket') }}
            button.button.is-link(type="submit" v-else disabled)
              i.fa.fa-cog.fa-spin
        .field
          p.is-small(v-t="'buy.info'")
</template>

<script>
  import SteamLogin from './SteamLogin.vue';
  export default {
    name: 'Buy',
    props: ['offeringId'],
    data () {
      return {
        offering: null,
        type: null,
        bought: false,
        sending: false,
        ticketDetails: {
          name: ''
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
        const ticketDetails = {
          ...self.ticketDetails,
          offeringId: self.offering.id,
          ownerId: self.$auth.getUser().id
        };
        self.$ticket.buy(ticketDetails).then(() => {
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
      isLoggedIn: function () {
        return this.$auth.isLoggedIn();
      },
      onSteamLogin: function () {
        this.$forceUpdate();
      }
    },
    mounted: function () {
      const self = this;
      self.$ticket.getOffering(this.offeringId).then(offering => {
        self.offering = offering;
        return self.$ticket.getType(offering.typeId);
      }).then(type => {
        self.type = type;
      });
    },
    components: {
      'steam-login': SteamLogin
    }
  };
</script>
