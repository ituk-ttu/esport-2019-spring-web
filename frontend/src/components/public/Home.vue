<template lang="pug">
  div
    .hero
      .container.center-content
        span
          h1: strong(v-t="'hero.date'")
          h2(v-t="'hero.game'")
          h2(v-t="'hero.teams'")
    .container.content-block
      h1.text-center(v-t="'tickets.tickets'")
      .text-center.center-content(v-if="$parent.tickets == null")
        i.fa.fa-2x.fa-cog.fa-spin
      .row.is-flex
        .col-lg-3.col-md-4.col-sm-6.col-xs-12(v-for="ticket in $parent.tickets" v-if="$parent.tickets != null")
          .ticket(v-if="isActive(ticket)")
            .ticket-content
              h2.ticket-name {{ $t('tickets.names["' + ticket.name + '"]') }}
              h3.ticket-price(v-if="hasActivePromotion(ticket)")
                | {{ getBestPromotion(ticket).cost / getBestPromotion(ticket).teamSize }}€
                small.text-default(v-if="getBestPromotion(ticket).teamSize > 1")  {{ $t('tickets.perPerson') }}
              p.ticket-description(v-if="hasActivePromotion(ticket)")
                | {{ $t('tickets.until') }}
                |  {{ getBestPromotion(ticket).availableUntil | moment("Do MMMM") }}
              h3.ticket-price(v-bind:class="hasActivePromotion(ticket) ? 'has-promotion' : ''")
                span.text-default(v-if="hasActivePromotion(ticket)") {{ $t('tickets.regularTicket') }}&nbsp
                | {{ ticket.cost / ticket.teamSize }}€
                small.text-default(v-if="ticket.teamSize > 1 && hasActivePromotion(ticket)")
                  |  {{ $t('tickets.perPersonShort') }}
                small.text-default(v-if="ticket.teamSize > 1 && !hasActivePromotion(ticket)")
                  |  {{ $t('tickets.perPerson') }}
              p.ticket-description(v-bind:class="hasActivePromotion(ticket)? 'has-promotion' : ''")
                | {{ $t('tickets.availableUntil') }}
                |  {{ ticket.availableUntil | moment("Do MMMM") }}
              p.ticket-remaining.text-lead(v-if="ticket.amountAvailable != null && getRemaining(ticket.amountAvailable, ticket.amountReserved) != null")
                | {{ getRemaining(ticket.amountAvailable, ticket.amountReserved) }}
                span(v-if="ticket.teamSize > 1")  {{ $t('tickets.teams') }}
                span(v-else)  {{ $t('tickets.pieces') }}
                |  {{ $t('tickets.remaining') }}
              p.ticket-out(v-if="ticket.amountAvailable != null && ticket.amountAvailable <= ticket.amountReserved")
                small {{ $t('tickets.outOfTickets') }}
              p.ticket-at-location-cost(v-if="ticket.atLocationCost != null") {{ $t('tickets.atLocation') }}
                span.text-primary  {{ ticket.atLocationCost / ticket.teamSize }}€
                small.text-default(v-if="ticket.teamSize > 1")  {{ $t('tickets.perPerson') }}
            router-link.buy-btn(:to="{ name: 'Buy', params: { ticketId: hasActivePromotion(ticket) ? getBestPromotion(ticket).id : ticket.id } }")
              span(v-if="ticket.amountAvailable != null && ticket.amountAvailable <= ticket.amountReserved")
                | {{ $t('tickets.notifyMe') }}
              span(v-else) {{ $t('tickets.buy') }}
    .container.content-block
      h1.text-center(v-t="'home.sponsors'")
      .row.is-flex.sponsors
        .col-xs-6: a(target="_blank" href="https://arvutitark.ee/"): img.img-responsive(src="../../assets/sponsors/arvutitark_logo.svg")
        .col-xs-6: a(target="_blank" href="https://www.msi.com"): img.img-responsive(src="../../assets/sponsors/msi-gaming.png")
        .col-xs-6: a(target="_blank" href="https://ttu.ee/itt"): img.img-responsive(src="../../assets/sponsors/itt.png")
        .col-xs-6: a(target="_blank" href="https://ituk.ee"): img.img-responsive(src="../../assets/sponsors/ituk.svg")
      .row.is-flex.sponsors-sm
        .col-xs-4.col-sm-3: a(target="_blank" href="https://noctua.at/"): img(src="../../assets/sponsors/noctua.jpg")
        .col-xs-4.col-sm-3: a(target="_blank" href="https://www.roccat.org"): img(src="../../assets/sponsors/roccat.jpg")
        .col-xs-4.col-sm-3: a(target="_blank" href="http://www.balsnack.ee/"): img(src="../../assets/sponsors/balsnack.svg")
        .col-xs-4.col-sm-3: a(target="_blank" href="https://wolt.com/"): img(src="../../assets/sponsors/wolt.png")
        .col-xs-4.col-sm-3: a(target="_blank" href="https://boom-esports.org/"): img(src="../../assets/sponsors/boommedia.png")
        .col-xs-4.col-sm-3: a(target="_blank" href="https://filmiklubi.ee/"): img(src="../../assets/sponsors/filmiklubi-03.svg")
        .col-xs-4.col-sm-3: a(target="_blank" href="https://www.monsterenergy.com/"): img(src="../../assets/sponsors/monster-02.svg")
        .col-xs-4.col-sm-3: a(target="_blank" href="http://www.tomorrow.ee/"): img(src="../../assets/sponsors/nt-logo.svg")
        .col-xs-4.col-sm-3: a(target="_blank" href="https://www.academichostel.com"): img(src="../../assets/sponsors/academic_hostel.svg")
        .col-xs-4.col-sm-3: a(target="_blank" href="http://www.bytelife.com/"): img(src="../../assets/sponsors/bytelife.png")
        .col-xs-4.col-sm-3: a(target="_blank" href="http://www.exitroom.ee/"): img(src="../../assets/sponsors/exit_room.jpg")
        .col-xs-4.col-sm-3: a(target="_blank" href="https://www.ivpluss.ee/"): img(src="../../assets/sponsors/iveco.jpg")
        .col-xs-4.col-sm-3: a(target="_blank" href="https://ttu.ee/ye"): img(src="../../assets/sponsors/ye.jpg")
</template>

<script>
  export default {
    name: 'Home',
    props: ['tickets'],
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
      hasActivePromotion: function (type) {
        return this.$ticket.hasActivePromotion(type);
      },
      getBestPromotion: function (ticket) {
        return ticket.promotions[0]; // TODO: find best promotion by type
      }
    }
  };
</script>
