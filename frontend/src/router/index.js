import Vue from 'vue';
import Router from 'vue-router';
import Public from '@/components/Public';
import Home from '@/components/public/Home';
import Contact from '@/components/public/Contact';
import Faq from '@/components/public/Faq';
import Buy from '@/components/public/Buy';
import TicketLogin from '@/components/TicketLogin';
import MyTickets from '@/components/public/MyTickets';
import Schedule from '@/components/public/Schedule';
import HouseRules from '@/components/public/HouseRules';
import Login from '@/components/public/Login';

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
          path: 'buy/:ticketId',
          name: 'Buy',
          component: Buy
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
        }
      ]
    },
    {
      path: '/ticketLogin/:loginKey',
      name: 'TicketLogin',
      component: TicketLogin
    }
  ]
});
