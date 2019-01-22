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

import { TableComponent, TableColumn } from 'vue-table-component';

import AuthService from './services/AuthService';
import TicketService from './services/TicketService';
import DocumentService from './services/DocumentService';

import './assets/style/style.scss';
const $ = require('jquery');

window.jQuery = $;

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

Vue.component('table-component', TableComponent);
Vue.component('table-column', TableColumn);

const config = {
  apiBase: process.env.apiBase,
  steamLoginReturnTo: process.env.steamLoginReturnTo
};
Vue.use(VueConfig, config);

Vue.use(VueResource);

Vue.http.options.root = config.apiBase;

Vue.use(AuthService);
Vue.use(TicketService);
Vue.use(DocumentService);

Vue.config.errorHandler = (err, vm, info) => {
  console.error({err, vm, info});
};

Vue.config.warnHandler = (err, vm, info) => {
  console.warn({err, vm, info});
};

/* eslint-disable no-new */
new Vue({
  el: '#app',
  i18n,
  router,
  template: '<App/>',
  components: {App},
  methods: {}
});
