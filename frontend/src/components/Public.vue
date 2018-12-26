<template lang="pug">
  #public
    navbar
    router-view.view
    .footer.center-content
      a.footer-link.footer-social(href="https://www.facebook.com/ttuesport/"): i.fa.fa-lg.fa-facebook
      a.footer-link.footer-social(href="https://www.instagram.com/ttuesport/"): i.fa.fa-lg.fa-instagram
      a.footer-link.footer-social(href="mailto:esport@ituk.ee"): i.fa.fa-lg.fa-envelope
      a.footer-link.footer-social(href="https://github.com/ituk-ttu/esport-spring-2018-web"): i.fa.fa-lg.fa-github
</template>

<script>
  import Navbar from './Navbar';

  export default {
    name: 'public',
    created () {
      this.$root.$i18n.locale = this.$root.$localStorage.get('language', 'et');
      this.$moment.locale(this.$root.$i18n.t('moment'));
    },
    data() {
      return {
        tickets: null
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
