<template lang="pug">
  #public
    navbar
    router-view.view
    footer.footer.main-footer
      .content.has-text-centered
        a.footer-link(href="https://www.facebook.com/taltech.esport/"): i.fa.fa-lg.fa-facebook
        a.footer-link(href="https://www.instagram.com/taltech.esport/"): i.fa.fa-lg.fa-instagram
        a.footer-link(href="mailto:esport@ituk.ee"): i.fa.fa-lg.fa-envelope
        a.footer-link(href="https://github.com/ituk-ttu/esport-2019-spring-web"): i.fa.fa-lg.fa-github
</template>

<script>
  import Navbar from './Navbar';

  export default {
    name: 'public',
    created () {
      this.$root.$i18n.locale = this.$root.$localStorage.get('language', 'et');
      this.$moment.locale(this.$root.$i18n.t('moment'));
    },
    data () {
      return {
        tickets: [{
          'name': 'CS:GO Team',
          'amountAvailable': 30,
          'cost': 100.00,
          'availableFrom': '2018-01-02T13:57:58+02:00',
          'availableUntil': '2018-02-04T23:59:59+02:00',
          'parentTicketTypeId': null,
          'teamSize': 5,
          'atLocationCost': 200,
          'amountReserved': 29,
          'promotions': [{
            'name': 'CS:GO Team Early Bird',
            'amountAvailable': 30,
            'cost': 75.00,
            'availableFrom': '2019-01-02T14:01:19+02:00',
            'availableUntil': '2019-01-14T23:59:59+02:00',
            'parentTicketTypeId': 1,
            'teamSize': 5,
            'atLocationCost': null,
            'amountReserved': 15,
            'id': 2,
            'perPersonCost': 15.00,
            'amountRemaining': 15
          }],
          'id': 1,
          'perPersonCost': 20.00,
          'amountRemaining': 1
        }, {
          'name': 'CS:GO VIP Team',
          'amountAvailable': 2,
          'cost': 150.00,
          'availableFrom': '2019-01-02T14:02:57+02:00',
          'availableUntil': '2019-02-04T23:59:59+02:00',
          'parentTicketTypeId': null,
          'teamSize': 5,
          'atLocationCost': null,
          'amountReserved': 2,
          'id': 3,
          'perPersonCost': 30.00,
          'amountRemaining': 0
        }, {
          'name': 'Regular Gamer',
          'amountAvailable': 20,
          'cost': 15.00,
          'availableFrom': '2018-01-02T14:03:53+02:00',
          'availableUntil': '2019-02-04T23:59:59+02:00',
          'parentTicketTypeId': null,
          'teamSize': 1,
          'atLocationCost': 25,
          'amountReserved': 0,
          'promotions': [{
            'name': 'Regular Gamer Early Bird',
            'amountAvailable': 20,
            'cost': 10.00,
            'availableFrom': '2018-01-02T14:06:03+02:00',
            'availableUntil': '2018-01-14T23:59:59+02:00',
            'parentTicketTypeId': 4,
            'teamSize': 1,
            'atLocationCost': null,
            'amountReserved': 0,
            'id': 5,
            'perPersonCost': 10.00,
            'amountRemaining': 20
          }],
          'id': 4,
          'perPersonCost': 15.00,
          'amountRemaining': 20
        }, {
          'name': 'Spectator',
          'amountAvailable': 99999,
          'cost': 5.00,
          'availableFrom': '2018-01-02T14:07:07+02:00',
          'availableUntil': '2018-02-04T23:59:59+02:00',
          'parentTicketTypeId': null,
          'teamSize': 1,
          'atLocationCost': 8,
          'amountReserved': 14,
          'id': 6,
          'perPersonCost': 5.00,
          'amountRemaining': 99985
        }]
      };
    },
    mounted: function () {
      const self = this;
      self.$ticket.getTypes().then(types => {
        self.tickets = types;
      });
    },
    components: {
      Navbar
    }
  };
</script>
