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
          ticket(v-for="ticket in $parent.tickets" v-if="$parent.tickets != null" :ticket="ticket" :key="ticket.id")
    section.section
      .container
        h1.title.has-text-centered.has-text-primary(v-t="'home.sponsors'")
        .columns.is-multiline.is-centered.is-vcentered
          sponsor(url="https://ituk.ee" image="ituk.svg" big)
          sponsor(url="https://ttu.ee/itt" image="itt.png" big)
          sponsor(url="https://ttu.ee/itt" image="ye.jpg" big)
        .columns.is-multiline.is-centered.is-vcentered
          sponsor(url="https://ituk.ee" image="filmiklubi-03.svg")
          sponsor(url="https://ttu.ee/itt" image="thorgate.png" big)
</template>

<script>
  import Ticket from './Ticket';
  import Sponsor from './Sponsor';

  export default {
    components: {Ticket, Sponsor},
    name: 'Home',
    props: ['tickets']
  };
</script>
