import Vue from 'vue';
import VueResource from 'vue-resource';

import jwtDecode from 'jwt-decode';

Vue.use(VueResource);

const AUTHORIZATION_HEADER = 'Authorization';
const USER_KEY = 'user';
const TOKEN_REFRESH_INTERVAL = 6000000; // TODO smaller

function AuthService (Vue) {
  const svc = {};

  const init = () => {
    Vue.http.interceptors.push(interceptor);
    setInterval(() => {
      if (!svc.isLoggedIn() || isTokenExpired() || shouldRefreshToken()) {
        return;
      }
      Vue.http.get('api/refreshToken'); // TODO use response
    }, TOKEN_REFRESH_INTERVAL);
  };

  svc.logIn = (user, token) => localStorage.setItem(USER_KEY, JSON.stringify({user, token}));

  svc.logOut = () => localStorage.removeItem(USER_KEY);

  svc.isLoggedIn = () => getUser() !== null;

  svc.getUser = () => getUser();

  svc.isAdmin = () => svc.isLoggedIn() && svc.getUser().role === 'ADMIN';

  svc.getSteamLoginLink = returnTo => {
    return Vue.http.get('api/steam/loginLink', { params: { returnTo } }).then(res => res.body);
  };

  svc.verifySteamLogin = returnUrl => {
    const params = {};
    for (let entry of new URL(returnUrl).searchParams) {
      params[entry[0]] = entry[1];
    }
    params['receivingUrl'] = returnUrl;
    let verifyResult = Vue.http.get('api/steam/verify', { params: params })
                          .then(res => res.body);
    verifyResult.then(({user, token}) => svc.logIn(user, token));
    return verifyResult;
  };

  const shouldRefreshToken = token => svc.isLoggedIn() &&
                                      isPassed((getClaims(token).exp + getClaims(token).iat) / 2);

  let isTokenExpired = token => isPassed(getClaims(token).exp - 5); // 5 seconds buffer

  const isPassed = unixSeconds => unixSeconds - Date.now() / 1000 < 0;

  const getToken = () => {
    return getAuthDetails() != null ? JSON.parse(getAuthDetails()).token : null;
  };

  const getClaims = () => {
    return getToken() != null ? jwtDecode(getToken()) : null;
  };

  const getUser = () => {
    return getAuthDetails() != null ? JSON.parse(getAuthDetails()).user : null;
  };

  const getAuthDetails = () => localStorage.getItem(USER_KEY);

  const interceptor = (request, next) => {
    if (svc.isLoggedIn()) {
      request.headers.set(AUTHORIZATION_HEADER, 'Bearer ' + getToken());
    }
    request.headers.set('Accept', 'application/json');
    next();
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
