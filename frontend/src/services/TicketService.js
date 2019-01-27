import Vue from 'vue';
import VueResource from 'vue-resource';
import moment from 'moment';

Vue.use(VueResource);

function TicketService (Vue) {
  const svc = {};
  let ticketTypes = null;

  svc.getTypes = () => {
    if (ticketTypes == null) {
      loadTicketTypes();
    }
    return ticketTypes;
  };

  svc.getVisibleOfferings = () => {
    // TODO: cache
    return Vue.http.get('api/tickets/offerings/visible').then(res => res.body);
  };

  svc.getOffering = id => {
    return Vue.http.get('api/tickets/offerings/' + id).then(res => res.body);
  };

  svc.getType = typeId => {
    return svc.getTypes().then(types => findType(typeId, types));
  };

  svc.isTypeActive = type => {
    const now = moment();
    const availableFrom = type.availableFrom;
    const availableUntil = type.availableUntil;
    return (availableFrom == null || now.isAfter(availableFrom)) &&
      (availableUntil == null || now.isBefore(availableUntil));
  };

  svc.hasEnded = type => {
    const now = moment();
    const availableUntil = type.availableUntil;
    return (availableUntil == null || now.isAfter(availableUntil));
  };

  svc.hasActivePromotion = type => {
    if (type.promotions == null) {
      return false;
    }
    return type.promotions.some(promotion => svc.isTypeActive(promotion));
  };

  svc.buy = (ticketDetails, typeId) => Vue.http.post('api/ticket',
    Object.assign({}, ticketDetails, {type: {id: typeId}}))
    .then(res => res.body);

  svc.adminCanConfirm = ticket => ticket.status === 'AWAITING_PAYMENT';

  svc.adminCanCancel = ticket => ticket.status !== 'CANCELED';

  svc.ownerCanCancel = ticket => ['IN_WAITING_LIST', 'AWAITING_PAYMENT'].includes(ticket.status);

  svc.confirm = ticket => Vue.http.post('api/ticket/' + ticket.id + '/confirm').then(res => res.body);

  svc.cancel = ticket => Vue.http.post('api/ticket/' + ticket.id + '/cancel').then(res => res.body);

  svc.getMyTickets = () => Vue.http.get('api/tickets/mine').then(res => res.body);

  svc.getAllTickets = () => Vue.http.get('api/tickets').then(res => res.body);

  svc.storeMember = (ticket, member) => Vue.http.post('api/ticket/' + ticket.id + '/member', member).then(res => {
    const newMember = res.body;
    if (member.id == null) {
      ticket.members.push(newMember);
    } else {
      let memberIndex = ticket.members.findIndex(curMember => curMember.id === member.id);
      ticket.members[memberIndex] = newMember;
    }
  });

  svc.removeMember = (ticket, member) => Vue.http.delete('api/ticket/' + ticket.id + '/member/' + member.id).then(res => {
    let memberIndex = ticket.members.findIndex(curMember => curMember.id === member.id);
    ticket.members.splice(memberIndex, 1);
  });

  function loadTicketTypes () {
    ticketTypes = Vue.http.get('api/tickets/types').then(res => res.body);
  }

  function findType (typeId, types) {
    if (types == null) {
      return null;
    }
    for (let type of types) {
      if (type.id === typeId) {
        return type;
      }
      let promotionMatch = findType(typeId, type.promotions);
      if (promotionMatch != null) {
        return promotionMatch;
      }
    }
    return null;
  }

  return svc;
}

const install = (Vue, options) => {
  let service = TicketService(Vue);
  Vue.prototype.$ticket = service;
  Vue.$ticket = service;
};

// Array.prototype.findIndex polyfill
// https://tc39.github.io/ecma262/#sec-array.prototype.findIndex
if (!Array.prototype.findIndex) {
  Object.defineProperty(Array.prototype, 'findIndex', { // eslint-disable-line no-extend-native
    value: function (predicate) {
      // 1. Let O be ? ToObject(this value).
      if (this == null) {
        throw new TypeError('"this" is null or not defined');
      }

      var o = Object(this);

      // 2. Let len be ? ToLength(? Get(O, "length")).
      var len = o.length >>> 0;

      // 3. If IsCallable(predicate) is false, throw a TypeError exception.
      if (typeof predicate !== 'function') {
        throw new TypeError('predicate must be a function');
      }

      // 4. If thisArg was supplied, let T be thisArg; else let T be undefined.
      var thisArg = arguments[1];

      // 5. Let k be 0.
      var k = 0;

      // 6. Repeat, while k < len
      while (k < len) {
        // a. Let Pk be ! ToString(k).
        // b. Let kValue be ? Get(O, Pk).
        // c. Let testResult be ToBoolean(? Call(predicate, T, « kValue, k, O »)).
        // d. If testResult is true, return k.
        var kValue = o[k];
        if (predicate.call(thisArg, kValue, k, o)) {
          return k;
        }
        // e. Increase k by 1.
        k++;
      }

      // 7. Return -1.
      return -1;
    }
  });
}

export default {install};
