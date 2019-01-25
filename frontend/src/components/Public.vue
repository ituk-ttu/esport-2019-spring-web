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
