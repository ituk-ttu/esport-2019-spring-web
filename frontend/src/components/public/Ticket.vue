<template lang="pug">
  .ticket.column.is-one-quarter-fullhd.is-one-third-desktop.is-half-tablet.is-full-mobile
    .card.full-height-card
      .card-content
        h2.title.has-text-primary {{ $t('tickets.names["' + ticket.name + '"]') }}
        h3.title.has-text-weight-bold(v-if="hasActivePromotion(ticket)")
          | {{ getBestPromotion(ticket).cost / getBestPromotion(ticket).teamSize }}€
          small.has-text-weight-normal(v-if="getBestPromotion(ticket).teamSize > 1")  {{ $t('tickets.perPerson') }}
        p.subtitle.has-text-burgundy(v-if="hasActivePromotion(ticket)")
          span(v-if="(isActive(ticket) || hasActivePromotion(ticket)) || hasEnded(ticket)")
            | {{ $t('tickets.until') }}
            |  {{ getBestPromotion(ticket).availableUntil | moment('Do MMMM') }}
          span(v-if="(!isActive(ticket) && !hasActivePromotion(ticket)) && !hasEnded(ticket)")
            | {{ $t('tickets.from') }}
            |  {{ getBestPromotion(ticket).availableFrom | moment('Do MMMM') }}
          div &nbsp
        h3.title.has-text-weight-bold(v-bind:class="hasActivePromotion(ticket) ? 'has-text-line-through is-5' : ''")
          span.text-default(v-if="hasActivePromotion(ticket)") {{ $t('tickets.regularTicket') }}&nbsp
          | {{ ticket.cost / ticket.teamSize }}€
          small.has-text-weight-normal(v-if="ticket.teamSize > 1 && hasActivePromotion(ticket)")
            |  {{ $t('tickets.perPersonShort') }}
          small.has-text-weight-normal(v-if="ticket.teamSize > 1 && !hasActivePromotion(ticket)")
            |  {{ $t('tickets.perPerson') }}
        h4.subtitle.is-6(v-bind:class="hasActivePromotion(ticket)? 'has-text-line-through' : ''")
          span(v-if="(isActive(ticket) || hasActivePromotion(ticket)) || hasEnded(ticket)")
            | {{ $t('tickets.availableUntil') }}
            |  {{ ticket.availableUntil | moment('Do MMMM') }}
          span(v-if="(!isActive(ticket) && !hasActivePromotion(ticket)) && !hasEnded(ticket)")
            | {{ $t('tickets.availableFrom') }}
            |  {{ ticket.availableFrom | moment('Do MMMM') }}
        p(v-if="ticket.atLocationCost != null") {{ $t('tickets.atLocation') }}
          span.has-text-weight-bold  {{ ticket.atLocationCost / ticket.teamSize }}€
          small.text-default(v-if="ticket.teamSize > 1")  {{ $t('tickets.perPerson') }}
        p.has-text-burgundy(v-if="ticket.amountAvailable != null && getRemaining(ticket.amountAvailable, ticket.amountReserved) != null")
          span.has-text-weight-bold {{ getRemaining(ticket.amountAvailable, ticket.amountReserved) }}
          span(v-if="ticket.teamSize > 1")  {{ $t('tickets.teams') }}
          span(v-else)  {{ $t('tickets.pieces') }}
          |  {{ $t('tickets.remaining') }}
        p.ticket-out(v-if="ticket.amountAvailable != null && ticket.amountAvailable <= ticket.amountReserved")
          small {{ $t('tickets.outOfTickets') }}
      footer.card-footer
        router-link.has-text-weight-bold.card-footer-item(:to="{ name: 'Buy', params: { ticketId: hasActivePromotion(ticket) ? getBestPromotion(ticket).id : ticket.id } }" v-if="(isActive(ticket) || hasActivePromotion(ticket)) && !hasEnded(ticket)")
          span(v-if="ticket.amountAvailable != null && ticket.amountAvailable <= ticket.amountReserved")
            | {{ $t('tickets.notifyMe') }}
          span(v-else) {{ $t('tickets.buy') }}
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
