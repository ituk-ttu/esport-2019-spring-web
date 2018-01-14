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

  svc.hasActivePromotion = type => {
    if (type.promotions == null) {
      return false;
    }
    return type.promotions.some(promotion => svc.isTypeActive(promotion));
  };

  svc.buy = (ticketDetails, typeId) => Vue.http.post('api/ticket',
                                                     Object.assign({}, ticketDetails, { type: { id: typeId } }))
                                               .then(res => res.body);

  svc.canConfirm = ticket => Vue.auth.isAdmin() && ticket.status === 'AWAITING_PAYMENT';

  svc.canCancel = ticket => (Vue.auth.isAdmin() && ticket.status !== 'CANCELED') ||
                            ['IN_WAITING_LIST', 'PAID'].includes(ticket.status);

  svc.confirm = ticket => Vue.http.post('api/ticket/' + ticket.id + '/confirm').then(res => res.body);

  svc.cancel = ticket => Vue.http.post('api/ticket/' + ticket.id + '/cancel').then(res => res.body);

  svc.getTickets = () => Vue.http.get('api/tickets').then(res => res.body);

  function loadTicketTypes () {
    ticketTypes = Vue.http.get('api/ticketTypes').then(res => res.body);
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

export default { install };
