<template lang="pug">
  div
    nav.navbar
      .navbar-brand
        a.navbar-item.navbar-logo(href="https://e-sport.ee"): img(src="../../assets/svg/logo.onpurple.svg")
        template(v-for="language in languages()")
          navbar-link(v-if="language !== currentLanguage()" :title="$t('navbar.lang.' + language)"
            @go="setLanguage(language)" look="bold")
    .container
      .section
        router-view.view
</template>

<script>
  import NavbarLink from '../presentation/navbar/NavbarLink';

  export default {
    name: 'TicketCertificateHome',
    components: { NavbarLink },
    created () {
      this.$root.$i18n.locale = this.$root.$localStorage.get('language', 'et');
      this.$moment.locale(this.$root.$i18n.t('moment'));
    },
    methods: {
      setLanguage: function (language) {
        this.$root.$i18n.locale = language;
        this.$moment.locale(this.$root.$i18n.t('moment'));
        this.$root.$localStorage.set('language', language);
      },
      languages() {
        return Object.keys(this.$root.$i18n.messages);
      },
      currentLanguage() {
        return this.$root.$i18n.locale;
      }
    }
  };
</script>

<style>
  .navbar-logo {
    flex-grow: 1;
  }
</style>
