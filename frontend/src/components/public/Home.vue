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
        .has-text-centered(v-if="offerings == null || types == null"): i.fa.fa-2x.fa-cog.fa-spin
        .columns.is-multiline.is-centered(v-else)
          ticket-offering-card(v-for="offering in offerings" :offering="offering"
                               :type="getType(offering.typeId)" :key="offering.id")
    section.section
      .container
        h1.title.has-text-centered.has-text-primary(v-t="'home.sponsors'")
        sponsor-grid(:names="bigSponsors" big)
        sponsor-grid(:names="smallSponsors")
</template>

<script>
  import TicketOfferingCard from './TicketOfferingCard';
  import SponsorGrid from '../presentation/sponsor/SponsorGrid';

  export default {
    components: {TicketOfferingCard, SponsorGrid},
    name: 'Home',
    data: function() {
      return {
        offerings: null,
        types: null,
        bigSponsors: [
          'ituk',
          'itt',
          'ye',
          'arvutitark',
          'lg',
          'belief',
          'networkTomorrow',
          'msi'
        ],
        smallSponsors: [
          'filmiklubi',
          'thorgate',
          'gamdias',
          'photopoint',
          'nissan',
          'roccat',
          'speedlink',
          'xgr'
        ]
      };
    },
    methods: {
      getType: function(id) {
        return this.types.find(type => type.id === id);
      }
    },
    mounted: function() {
      const self = this;
      self.$ticket.getVisibleOfferings().then(offerings => {
        self.offerings = offerings; // TODO: Loading, error
      });
      self.$ticket.getTypes().then(types => {
        self.types = types; // TODO: Loading, error
      });
    }
  };
</script>
