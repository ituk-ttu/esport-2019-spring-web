import Vue from 'vue';
import VueResource from 'vue-resource';

import jwtDecode from 'jwt-decode';

Vue.use(VueResource);

const AUTHORIZATION_HEADER = 'Authorization';
const AUTH_TOKEN = 'authToken';
const TOKEN_REFRESH_INTERVAL = 6000;

const AuthService = (Vue) => {
  const self = this;

  const init = () => {
    Vue.http.interceptors.push(interceptor);
    setInterval(() => {
      if (!self.isLoggedIn() || self.isTokenExpired() || !self.shouldRefreshToken()) {
        return;
      }
      Vue.http.get("api/refreshToken");
    }, TOKEN_REFRESH_INTERVAL);
  };

  self.removeToken = () => localStorage.removeItem(AUTH_TOKEN);

  self.getToken = () => {
    let token = getToken();
    if (token !== null && self.isTokenExpired()) {
      self.removeToken();
    }
    return token;
  };

  self.isLoggedIn = () => self.getToken() !== null;

  self.isAdmin = () => self.isLoggedIn() && self.getClaims().admin === true;

  self.setToken = token => localStorage.setItem(AUTH_TOKEN, token);

  self.getClaims = token => {
    token = token || getToken();
    if (token === null || !token.startsWith("Bearer ")) {
      return null;
    }
    return jwtDecode(token.substring(7));
  };

  self.isTokenExpired = token => isPassed(self.getClaims(token).exp - 5); // 5 seconds buffer

  self.shouldRefreshToken = token => self.isLoggedIn() &&
                                     isPassed((self.getClaims(token).exp + self.getClaims(token).iat) / 2);

  const isPassed = unixSeconds => unixSeconds - Date.now() / 1000 < 0;

  const getToken = () => localStorage.getItem(AUTH_TOKEN);

  const interceptor = (request, next) => {
    if (self.isLoggedIn()) {
      request.headers.set(AUTHORIZATION_HEADER, self.getToken());
    }
    request.headers.set('Accept', 'application/json');
    next(result => {
      let authToken = result.headers.get(AUTHORIZATION_HEADER);
      if (authToken) {
        self.setToken(authToken);
      }
    });
  };

  init();
};

const install = (Vue, options) => {
  let service = AuthService(Vue);
  Vue.prototype.$authService = service;
  Vue.$authService = service;
};

export default { install };
