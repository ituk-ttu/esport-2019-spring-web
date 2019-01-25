<template lang="pug">
  span
    section.hero.is-large.home-hero
      .hero-body
        .container.has-text-centered
          h1.title.has-text-white.is-1(v-t="'hero.date'")
          h2.title.has-text-white.is-3(v-t="'hero.game'")
          h3.title.has-text-white.is-5(v-t="'hero.teams'")
    section.section
      .container
        h1.title.has-text-centered.has-text-primary.is-1(v-t="'tickets.tickets'")
        .has-text-centered(v-if="offerings == null"): i.fa.fa-2x.fa-cog.fa-spin
        .columns.is-multiline.is-centered
          ticket-offering-card(v-for="offering in offerings" v-if="offerings != null" :offering="offering"
                               :key="offering.id")
    section.section
      .container
        h1.title.has-text-centered.has-text-primary(v-t="'home.sponsors'")
        .columns.is-multiline.is-centered.is-vcentered
          sponsor(url="https://ituk.ee" image="ituk.svg" big)
          sponsor(url="https://ttu.ee/itt" image="itt.png" big)
          sponsor(url="https://ttu.ee/ye" image="ye.jpg" big)
        .columns.is-multiline.is-centered.is-vcentered
          sponsor(url="https://filmiklubi.ee" image="filmiklubi-03.svg")
          sponsor(url="https://thorgate.eu" image="thorgate.png")
</template>

<script>
  import TicketOfferingCard from './TicketOfferingCard';
  import Sponsor from './Sponsor';

  export default {
    components: {TicketOfferingCard, Sponsor},
    name: 'Home',
    data: function() {
      return {
        offerings: null
      }
    },
    mounted: function() {
      const self = this;
      self.$ticket.getVisibleOfferings().then(offerings => {
        self.offerings = offerings; //TODO: Loading, error
      });
    }
  };
</script>
