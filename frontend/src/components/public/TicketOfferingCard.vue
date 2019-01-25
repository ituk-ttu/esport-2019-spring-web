<template lang="pug">
  .ticket.column.is-one-quarter-fullhd.is-one-third-desktop.is-half-tablet.is-full-mobile
    .card.full-height-card
      .card-content
        h2.title.has-text-primary {{ offering.name }}
        h3.title.has-text-weight-bold
          | {{ offering.cost }}€
        h4.subtitle(v-if="isTeamType")
          | {{ costPerMember }}€
          | {{ $t('tickets.perPerson') }}
        p.has-text-burgundy
          span(v-if="isActive")
            | {{ $t('tickets.until') }}
            | {{ availableUntilDisplay | moment('Do MMMM') }}
          span(v-else)
            | {{ $t('tickets.from') }}
            | {{ offering.availableFrom | moment('Do MMMM')}}
          div &nbsp

</template>

<script>
  export default {
    name: 'TicketOfferingCard',
    props: ['offering', 'type'],
    computed: {
      isActive: function() {
        return !this.$moment(this.offering.availableFrom).isAfter(this.$moment()) &&
               this.$moment(this.offering.availableUntil).isAfter(this.$moment());
      },
      isTeamType: function() {
        return this.type.teamSize > 1;
      },
      costPerMember: function() {
        return this.offering.cost / this.type.teamSize;
      },
      availableUntilDisplay: function() {
        return this.$moment(this.offering.availableUntil).subtract(1, 'second').format();
      }

    }
  };
</script>
