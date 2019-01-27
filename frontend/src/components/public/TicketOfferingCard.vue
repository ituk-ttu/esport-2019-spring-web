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
            | {{ $t('tickets.availableUntil') }}
            | {{ availableUntilDisplay | moment('Do MMMM') }}
          span(v-else)
            | {{ $t('tickets.availableFrom') }}
            | {{ offering.availableFrom | moment('Do MMMM')}}
          div &nbsp
        p.has-text-burgundy(v-if="isActive")
          span.has-text-weight-bold {{ offering.amountRemaining }}
          span(v-if="type.teamSize > 1")  {{ $t('tickets.teams') }}
          span(v-else)  {{ $t('tickets.pieces') }}
          |  {{ $t('tickets.remaining') }}
        p.ticket-out(v-if="isActive && isSoldOut")
          small {{ $t('tickets.outOfTickets') }}
      footer.card-footer
        router-link.has-text-weight-bold.card-footer-item(:to="{name: 'Buy', params: {}}"
                                                          v-if="isActive")
          // TODO: Change text
          span(v-if="isSoldOut") {{ $t('tickets.notifyMe') }}
          span(v-else) {{ $t('tickets.buy') }}

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
      },
      isSoldOut: function() {
        return this.offering.amountRemaining <= 0;
      }
    }
  };
</script>
