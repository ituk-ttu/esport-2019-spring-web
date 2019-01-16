<template lang="pug">
  nav.navbar
    .navbar-start
      .navbar-brand
        a.navbar-item(href="https://e-sport.ee"): img(src="../assets/svg/logo.onpurple.svg")
        a.navbar-burger.burger(role='button' aria-label='menu' aria-expanded='false' data-target='menu')
          span(aria-hidden='true')
          span(aria-hidden='true')
          span(aria-hidden='true')
    .navbar-menu#menu
      .navbar-end
        navbar-link(:title="$t('navbar.home')" target-page="Home")
        navbar-dropdown(:title="$t('navbar.info')")
          navbar-link(:title="$t('navbar.faq')" target-page="Faq")
          navbar-link(:title="$t('navbar.timetable')" target-page="Schedule")
          navbar-link(:title="$t('navbar.houseRules')" target-page="HouseRules")
          navbar-link(:title="$t('navbar.csgoRules')" target-page="CsgoRules")
        navbar-dropdown(v-if="isLoggedIn" :title="username")
          navbar-link(:title="$t('navbar.myTickets')" target-page="MyTickets")
          navbar-link(:title="$t('navbar.logOut')" @go="logOut()")
        navbar-link(:title="$t('navbar.contact')" target-page="Contact")
        navbar-dropdown(v-if="isAdmin" :title="$t('navbar.admin')")
          navbar-link(:title="$t('navbar.adminTickets')" target-page="AdminTickets")
        navbar-link(v-else :title="$t('navbar.login')" target-page="Login")
        navbar-link(:title="$t('navbar.discord')" target-url="https://discord.gg/W5Psxu3" new-window
                    look="discord")
      template(v-for="language in languages")
        navbar-link(v-if="language !== currentLanguage" :title="$t('navbar.lang.' + language)"
                    @go="setLanguage(language)" look="bold")
</template>

<script>
  import NavbarLink from './presentation/navbar/NavbarLink';
  import NavbarDropdown from './presentation/navbar/NavbarDropdown';

  export default {
    name: 'navbar',
    methods: {
      setLanguage: function (language) {
        this.$root.$i18n.locale = language;
        this.$moment.locale(this.$root.$i18n.t('moment'));
        this.$root.$localStorage.set('language', language);
      },
      logOut: function () {
        this.$auth.removeToken();
        this.$router.push({ name: 'Login' });
      }
    },
    computed: {
      languages() {
        return Object.keys(this.$root.$i18n.messages);
      },
      currentLanguage() {
        return this.$root.$i18n.locale;
      },
      username() {
        const steamUser = this.$auth.getClaims()['steam_user'];
        return steamUser != null ? steamUser.name : this.$t('navbar.defaultUsername');
      },
      isAdmin() {
        if (this.isLoggedIn === false) {
          return false;
        }
        const admin = this.$auth.getClaims()['admin'];
        return admin !== null ? admin : false;
      },
      isLoggedIn() {
        return this.$auth.isLoggedIn();
      }
    },
    created () {
      this.$root.$i18n.locale = this.$root.$localStorage.get('language', 'et');
      this.$moment.locale(this.$root.$i18n.t('moment'));
    },
    components: {
      NavbarLink,
      NavbarDropdown
    }
  };
</script>
