import Vue from 'vue';
import Router from 'vue-router';
import Public from '@/components/Public';
import Home from '@/components/public/Home';
import Contact from '@/components/public/Contact';
import Faq from '@/components/public/Faq';
import Buy from '@/components/public/Buy';
import MyTickets from '@/components/public/MyTickets';
import Schedule from '@/components/public/Schedule';
import HouseRules from '@/components/public/HouseRules';
import CsgoRules from '@/components/public/CsgoRules';
import Fifa from '@/components/public/Fifa'
import Login from '@/components/public/Login';
import Admin from '@/components/public/Admin';
import Tickets from '@/components/public/admin/Tickets';
import TicketCertificateHome from '../components/ticket-cert/TicketCertificateHome';
import TicketCertificateView from '../components/ticket-cert/TicketCertificateView';
import CertAdmin from "../components/public/admin/CertAdmin";

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/',
      component: Public,
      children: [
        {
          path: '',
          name: 'Home',
          component: Home
        },
        {
          path: 'contact',
          name: 'Contact',
          component: Contact
        },
        {
          path: 'faq',
          name: 'Faq',
          component: Faq
        },
        {
          path: 'schedule',
          name: 'Schedule',
          component: Schedule
        },
        {
          path: 'houseRules',
          name: 'HouseRules',
          component: HouseRules
        },
        {
          path: 'csgoRules',
          name: 'CsgoRules',
          component: CsgoRules
        },
        {
          path: 'fifa',
          name: 'Fifa',
          component: Fifa
        },
        {
          path: 'buy/:offeringId',
          name: 'Buy',
          component: Buy,
          props: true
        },
        {
          path: 'myTickets',
          name: 'MyTickets',
          component: MyTickets
        },
        {
          path: 'login',
          name: 'Login',
          component: Login
        },
        {
          path: 'admin',
          name: 'Admin',
          component: Admin,
          children: [
            {
              path: 'tickets',
              name: 'AdminTickets',
              component: Tickets
            },
            {
              path: 'certs',
              name: 'CertAdmin',
              component: CertAdmin
            }
          ]
        }
      ]
    },
    {
      path: '/ticket-certificates',
      name: 'TicketCertificateHome',
      component: TicketCertificateHome,
      children: [
        {
          path: ':ticketCertCode',
          name: 'TicketCertView',
          component: TicketCertificateView,
          props: true
        }
      ]
    }
  ]
});
