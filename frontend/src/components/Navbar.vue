<template lang="pug">
  nav.navbar
    .navbar-start
      .navbar-brand
        a.navbar-item(href="https://e-sport.ee"): img(src="../assets/svg/logo.onpurple.svg")
        a.navbar-burger.burger(role='button' aria-label='menu' aria-expanded='false' data-target='menu' v-on:click="isExpanded = !isExpanded")
          span(aria-hidden='true')
          span(aria-hidden='true')
          span(aria-hidden='true')
    .navbar-menu#menu(v-bind:class="isExpanded ? 'is-active' : ''")
      .navbar-end
        navbar-link(:title="$t('navbar.home')" target-page="Home")
        navbar-dropdown(:title="$t('navbar.info')")
          navbar-link(:title="$t('navbar.faq')" target-page="Faq")
          navbar-link(:title="$t('navbar.timetable')" target-page="Schedule")
          navbar-link(:title="$t('navbar.houseRules')" target-page="HouseRules")
          navbar-link(:title="$t('navbar.csgoRules')" target-page="CsgoRules")
        navbar-link(:title="$t('navbar.contact')" target-page="Contact")
        navbar-link(:title="$t('navbar.volunteer')" target-url="https://volunteer.e-sport.ee" new-window)

        navbar-dropdown(v-if="isLoggedIn()" :title="username()")
          navbar-link(:title="$t('navbar.myTickets')" target-page="MyTickets")
          navbar-link(:title="$t('navbar.removeUser')" @go="logOut()")
        navbar-link(v-else :title="$t('navbar.login')" target-page="Login")
        navbar-dropdown(v-if="isAdmin()" :title="$t('navbar.admin')")
          navbar-link(:title="$t('navbar.adminTickets')" target-page="AdminTickets")


        navbar-link(:title="$t('navbar.discord')" target-url="https://discord.gg/W5Psxu3" new-window
                    look="discord")
      template(v-for="language in languages()")
        navbar-link(v-if="language !== currentLanguage()" :title="$t('navbar.lang.' + language)"
                    @go="setLanguage(language)" look="bold")
</template>

<script>
  import NavbarLink from './presentation/navbar/NavbarLink';
  import NavbarDropdown from './presentation/navbar/NavbarDropdown';

  export default {
    name: 'navbar',
    data() {
      return {
        isExpanded: false
      };
    },
    methods: {
      setLanguage: function (language) {
        this.$root.$i18n.locale = language;
        this.$moment.locale(this.$root.$i18n.t('moment'));
        this.$root.$localStorage.set('language', language);
      },
      logOut: function () {
        this.$auth.logOut();
        this.$router.push({ name: 'Login' });
      },
      languages() {
        return Object.keys(this.$root.$i18n.messages);
      },
      currentLanguage() {
        return this.$root.$i18n.locale;
      },
      username() {
        return this.$auth.getUser().name;
      },
      isAdmin() {
        return this.$auth.isAdmin();
      },
      isLoggedIn() {
        return this.$auth.isLoggedIn();
      }
    },
    created () {
      this.$root.$i18n.locale = this.$root.$localStorage.get('language', 'et');
      this.$moment.locale(this.$root.$i18n.t('moment'));
      const self = this;
      self.$auth.getEventBus().$on('authChange', () => {
        self.$forceUpdate();
      });
    },
    components: {
      NavbarLink,
      NavbarDropdown
    }
  };
</script>
