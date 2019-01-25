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
    name: 'Ticket',
    props: ['ticket'],
    methods: {
      getRemaining: function (total, reserved) {
        if ((reserved / total) + (1 / total) >= 0.9) {
          return total > reserved ? total - reserved : 0;
        } else {
          return null;
        }
      },
      isActive: function (type) {
        return this.$ticket.isTypeActive(type);
      },
      hasEnded: function (type) {
        return this.$ticket.hasEnded(type) &&
          !(this.hasActivePromotion(type) && this.hasEnded(this.hasActivePromotion(type)));
      },
      hasActivePromotion: function (type) {
        return this.$ticket.hasActivePromotion(type);
      },
      getBestPromotion: function (ticket) {
        return ticket.promotions[0];
      }
    }
  };
</script>
