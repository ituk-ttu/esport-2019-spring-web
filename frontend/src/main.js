// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import router from './router';
import VueResource from 'vue-resource';
import VueConfig from 'vue-config';

import Vuex from 'vuex';
import * as VueGoogleMaps from 'vue2-google-maps';
import i18n from './language';
import App from './App';
import VueLocalStorage from 'vue-localstorage';
import VueAnalytics from 'vue-analytics';
import Notifications from 'vue-notification';

import AuthService from './services/AuthService';
import TicketService from './services/TicketService';

import './assets/less/style.less';

const $ = require('jquery');

window.jQuery = $;
require('bootstrap-less/js/bootstrap');

Vue.use(VueLocalStorage);
Vue.use(Vuex);
Vue.config.productionTip = false;
Vue.use(VueGoogleMaps, {
  load: {
    key: 'AIzaSyAuldH1cG6aVHzWpmxdGAIiHvaBXzZAqPc'
  }
});

const moment = require('moment');
require('moment/locale/et');
require('moment/locale/en-gb');
Vue.use(require('vue-moment'), {
  moment
});

Vue.use(VueAnalytics, {
  id: 'UA-111484189-1',
  router
});

Vue.use(Notifications);

const config = {
  apiBase: process.env.apiBase,
  steamLoginReturnTo: process.env.steamLoginReturnTo
};
Vue.use(VueConfig, config);

Vue.use(VueResource);

Vue.http.options.root = config.apiBase;

Vue.use(AuthService);
Vue.use(TicketService);

/* eslint-disable no-new */
new Vue({
  el: '#app',
  i18n,
  router,
  template: '<App/>',
  components: {App},
  methods: {}
});
