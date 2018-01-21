import Vue from 'vue';
import VueResource from 'vue-resource';

import jwtDecode from 'jwt-decode';

Vue.use(VueResource);

const AUTHORIZATION_HEADER = 'Authorization';
const AUTH_TOKEN = 'authToken';
const TOKEN_REFRESH_INTERVAL = 6000;

function AuthService (Vue) {
  const svc = {};

  const init = () => {
    Vue.http.interceptors.push(interceptor);
    setInterval(() => {
      if (!svc.isLoggedIn() || svc.isTokenExpired() || !svc.shouldRefreshToken()) {
        return;
      }
      Vue.http.get('api/refreshToken');
    }, TOKEN_REFRESH_INTERVAL);
  };

  svc.removeToken = () => localStorage.removeItem(AUTH_TOKEN);

  svc.getToken = () => {
    console.log(Vue);
    console.log(Object.keys(Vue));
    console.log(Vue.util);
    let token = getToken();
    if (token !== null && svc.isTokenExpired()) {
      svc.removeToken();
    }
    return token;
  };

  svc.isLoggedIn = () => svc.getToken() !== null;

  svc.isAdmin = () => svc.isLoggedIn() && svc.getClaims().admin === true;

  svc.setToken = token => localStorage.setItem(AUTH_TOKEN, token);

  svc.getClaims = token => {
    token = token || getToken();
    if (token === null || !token.startsWith('Bearer ')) {
      return null;
    }
    return jwtDecode(token.substring(7));
  };

  svc.isTokenExpired = token => isPassed(svc.getClaims(token).exp - 5); // 5 seconds buffer

  svc.shouldRefreshToken = token => svc.isLoggedIn() &&
                                     isPassed((svc.getClaims(token).exp + svc.getClaims(token).iat) / 2);

  svc.getSteamLoginLink = returnTo => {
    return Vue.http.get('api/steam/loginLink', { params: { returnTo } }).then(res => res.body);
  };

  svc.verifySteamLogin = returnUrl => {
    const params = {};
    for (let entry of new URL(returnUrl).searchParams) {
      params[entry[0]] = entry[1];
    }
    params['receivingUrl'] = returnUrl;
    return Vue.http.get('api/steam/verify', { params: params }).then(res => {
      return res.body;
    });
  };

  svc.performEmailLinkLogin = loginKey => Vue.http.get('api/ticket/token/' + loginKey).then(res => res.body);

  const isPassed = unixSeconds => unixSeconds - Date.now() / 1000 < 0;

  const getToken = () => localStorage.getItem(AUTH_TOKEN);

  const interceptor = (request, next) => {
    if (svc.isLoggedIn()) {
      request.headers.set(AUTHORIZATION_HEADER, svc.getToken());
    }
    request.headers.set('Accept', 'application/json');
    next(result => {
      let authToken = result.headers.get(AUTHORIZATION_HEADER);
      if (authToken) {
        svc.setToken(authToken);
      }
    });
  };

  init();

  return svc;
}

const install = (Vue, options) => {
  let service = AuthService(Vue);
  Vue.prototype.$auth = service;
  Vue.$auth = service;
};

export default { install };
