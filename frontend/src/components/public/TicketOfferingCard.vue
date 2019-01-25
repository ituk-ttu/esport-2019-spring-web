<template lang="pug">
  .ticket.column.is-one-quarter-fullhd.is-one-third-desktop.is-half-tablet.is-full-mobile
    .card.full-height-card
      .card-content
        h2.title.has-text-primary {{ offering.name }}
        h3.title.has-text-weight-bold
          | {{ offering.cost }}€
          // TODO: More info (revert)
</template>

<script>
  export default {
    name: 'TicketOfferingCard',
    props: ['offering'],
    methods: {
    }
  };
</script>
