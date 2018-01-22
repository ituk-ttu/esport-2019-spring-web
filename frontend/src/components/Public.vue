<template lang="pug">
  #public
    .navbar.navbar-default.navbar-static-top
      .container
        .navbar-header
          button.navbar-toggle.collapsed(type="button" data-toggle="collapse" data-target=".navbar-collapse")
            span.sr-only Toggle navigation
            span.icon-bar
            span.icon-bar
            span.icon-bar
          router-link.navbar-brand(to="/"): img.navbar-logo(src="../assets/svg/logo.long.svg")
        .collapse.navbar-collapse
          ul.nav.navbar-nav.navbar-right
            li: router-link(:to="{ name: 'Home' }") {{ $t('navbar.home') }}
            li: router-link(:to="{ name: 'Contact' }") {{ $t('navbar.contact') }}
            li.dropdown
              a.dropdown-toggle(href="#" data-toggle="dropdown" role="button" aria-expanded="false") Info&nbsp
                span.caret
              ul.dropdown-menu(role="menu")
                li: router-link(:to="{ name: 'Faq' }") {{ $t('navbar.faq') }}
                li: router-link(:to="{ name: 'Schedule' }") {{ $t('navbar.timetable') }}
                li: router-link(:to="{ name: 'HouseRules' }") {{ $t('navbar.houseRules') }}
            li(v-if="isLoggedIn()").dropdown
              a.dropdown-toggle(href="#" data-toggle="dropdown" role="button" aria-expanded="false")
                span.nav-steam-username {{ getUsername() }}
                | &nbsp
                span.caret.nav-user-caret
              ul.dropdown-menu(role="menu")
                li: router-link(:to="{ name: 'MyTickets' }") {{ $t('navbar.myTickets') }}
                li: a(href="#" v-on:click="logOut()") {{ $t('navbar.logOut') }}
            li(v-if="isAdmin()").dropdown
              a.dropdown-toggle(href="#" data-toggle="dropdown" role="button" aria-expanded="false")
                span.nav-steam-username {{ $t('navbar.admin') }}
                | &nbsp
                span.caret.nav-user-caret
              ul.dropdown-menu(role="menu")
                li: router-link(:to="{ name: 'AdminTickets' }") {{ $t('navbar.myTickets') }}
            li(v-else): router-link(:to="{ name: 'Login' }") {{ $t('navbar.login') }}
            li: a.link-discord(href="https://discord.gg/W5Psxu3" target="_blank"): strong Discord
            li: a(href="#", v-on:click.stop.prevent="setLanguage('en')" v-if="getLanguage != 'en'"): strong EN
            li: a(href="#", v-on:click.stop.prevent="setLanguage('et')" v-if="getLanguage != 'et'"): strong ET
    router-view.view
    .footer.center-content
      a.footer-link.footer-social(href="https://www.facebook.com/ttuesport/"): i.fa.fa-lg.fa-facebook
      a.footer-link.footer-social(href="https://www.instagram.com/ttuesport/"): i.fa.fa-lg.fa-instagram
      a.footer-link.footer-social(href="mailto:esport@ituk.ee"): i.fa.fa-lg.fa-envelope
      a.footer-link.footer-social(href="https://github.com/ituk-ttu/esport-spring-2018-web"): i.fa.fa-lg.fa-github
</template>

<script>
  export default {
    name: 'public',
    methods: {
      setLanguage: function (language) {
        this.$root.$i18n.locale = language;
        this.$moment.locale(this.$root.$i18n.t('moment'));
        this.$root.$localStorage.set('language', language);
      },
      getUsername: function () {
        const steamUser = this.$auth.getClaims()['steam_user'];
        return steamUser !== null ? steamUser.name : this.$t('navbar.defaultUsername');
      },
      isAdmin: function () {
        if(this.isLoggedIn() === false) {
          return false;
        }
        const admin = this.$auth.getClaims()['admin'];
        return admin !== null ? admin : false;
      },
      showSteamLoggedIn: function () {
        this.$notify({
          title: this.$t('login.steam.title'),
          text: this.$t('login.steam.text'),
          classes: 'black'
        });
      },
      isLoggedIn: function () {
        return this.$auth.isLoggedIn();
      },
      logOut: function () {
        this.$auth.removeToken();
        this.$router.push({ name: 'Login' });
      },
      getTicketTypeNameById: function (id) {
        for (let i = 0; i < this.tickets.length; i++) {
          if (this.tickets[i].id === parseInt(id)) {
            return this.tickets[i].name;
          } else if (this.tickets[i].hasOwnProperty('promotions')) {
            for (let j = 0; j < this.tickets[i].promotions.length; j++) {
              if (this.tickets[i].promotions[j].id === parseInt(id)) {
                return this.tickets[i].promotions[j].name;
              }
            }
          }
        }
        return 'N/A';
      }
    },
    computed: {
      getLanguage: function () {
        return this.$root.$i18n.locale;
      }
    },
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
    }
  };
</script>
