<template lang="pug">
  .ticket.column.is-one-quarter-fullhd.is-one-third-desktop.is-half-tablet.is-full-mobile
    .card.full-height-card
      .card-content
        h2.title.has-text-primary {{ offering.name }}
        h3.title.has-text-weight-bold.has-text-dark
          | {{ offering.cost }}€
        h4.subtitle.has-text-dark(v-if="isTeamType")
          | {{ costPerMember }}€
          | {{ $t('tickets.perPerson') }}
        p.has-text-burgundy
          span(v-if="isActive")
            | {{ $t('tickets.availableUntil') }}
            | {{ availableUntilDisplay | moment('Do MMMM HH:mm') }}
          span(v-else)
            | {{ $t('tickets.availableFrom') }}
            | {{ offering.availableFrom | moment('Do MMMM HH:mm')}}
          div &nbsp
        h6.title.is-6.has-text-dark.small-margin-bottom {{ $t('tickets.includes') }}
          small(v-if="isTeamType")  {{ $t('tickets.perPerson') }}
          | :
        .content.md-content.small-margin-bottom.is-size-7(v-html="included")
        p.has-text-burgundy(v-if="isActive && !isSoldOut && offering.amountRemaining != null && offering.amountRemaining < 5")
          span.has-text-weight-bold {{ offering.amountRemaining }}
          span(v-if="type.teamSize > 1")  {{ $t('tickets.teams') }}
          span(v-else)  {{ $t('tickets.pieces') }}
          |  {{ $t('tickets.remaining') }}
        p.ticket-out(v-if="isActive && isSoldOut && offering.amountRemaining != null")
          small {{ $t('tickets.outOfTickets') }}
      footer.card-footer(v-if="isActive")
        span.has-text-weight-bold.has-text-light.has-text-centered.card-footer-item(v-if="!offering.availableOnline")
          | {{ $t('tickets.notAvailableOnline') }}
        router-link.has-text-weight-bold.has-text-centered.card-footer-item(
            :to="{name: 'Buy', params: {offeringId: offering.id}}" v-else)
          span(v-if="isSoldOut") {{ $t('tickets.buyWaitingList') }}
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
    },
    data () {
      return {
        included: ''
      };
    },
    methods: {
      loadFaq: function () {
        let self = this;
        this.$document.getDocument('includedInTickets/' + self.type.code, this.$root.$i18n.locale).then(doc => { this.included = doc; });
      }
    },
    mounted: function () {
      this.loadFaq();
    },
    beforeUpdate: function () {
      this.loadFaq();
    }
  };
</script>
