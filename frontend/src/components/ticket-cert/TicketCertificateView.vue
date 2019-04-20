<template lang="pug">
  div
    template(v-if="status === 'SHOW_CODE'")
      h1.title(v-t="'tickets.certs.redeem.title'")
      p.sub-title(v-t="'tickets.certs.redeem.guide'")
      .qr
        vue-qr(:text="ticketCertCode" :dotScale="1.0" :margin="0" :size="378")
      p.code {{ ticketCertCode }}
    template(v-else-if="status === 'ACCEPT'")
      h1.title(v-t="'navbar.houseRules'")
      h2.sub-title(v-t="'acceptRulesScroll'")
      .card.content.md-content(v-html="rules")
      p(v-t="'tickets.certs.redeem.guide'")
      button.button.is-success(v-t="'acceptRules'" @click="onAccept")
    template(v-else-if="status === 'WAIT'")
      h1.title(v-t="'tickets.certs.redeem.title'")
    template(v-else-if="status === 'OK'")
      h1.title(v-t="'tickets.certs.redeem.title'")

</template>

<script>
  import VueQr from 'vue-qr';
  import Vue from 'vue';

  const SHOW_CODE = 'SHOW_CODE';
  const ACCEPT = 'ACCEPT';
  const WAIT = 'WAIT';
  const OK = 'OK';

  export default {
    name: 'TicketCertificateHome',
    props: ['ticketCertCode'],
    components: { VueQr },
    data () {
      return {
        status: SHOW_CODE,
        rules: null
      };
    },
    created () {
      this.$root.$i18n.locale = this.$root.$localStorage.get('language', 'et');
      this.$moment.locale(this.$root.$i18n.t('moment'));
    },
    mounted () {
      const self = this;
      const evtSource = new EventSource(self.$config.apiBase + '/api/certs/' + this.ticketCertCode + '/clientEvents');
      evtSource.addEventListener('EVENT', (e) => {
        const event = JSON.parse(e.data);
        if (event === 'ASK_ACCEPTANCE') {
          self.status = ACCEPT;
        }
        if (event === 'FINISHED') {
          self.status = OK;
        }
      });
      this.$document.getDocument('houseRules', this.$root.$i18n.locale).then(doc => { this.rules = doc; });
    },
    methods: {
      onAccept: function () {
        this.status = WAIT;
        Vue.http.post('api/certs/' + this.ticketCertCode + '/giveAcceptance').then(res => {
          console.log(res);
        }).catch(console.log);
      }
    }
  };
</script>

<style>
  .qr {
    margin: 20px auto;
    text-align: center;
  }
  .code {
    font-size: 42pt;
    text-align: center;
    font-weight: bold;
  }
</style>
