<template lang="pug">
  .section
    .notification.is-error(v-if="error != null") {{ error }}
    template(v-if="status === 'SCAN'")
      qrcode-stream(@decode="onDecode" @onInit="onInit")
      .field
        label.label Sisesta kood manuaalselt
        .control
          input.input(v-model="manualCode")
      .modal-card-foot
        .field
          .control
            button.button.is-link(type="submit" @click="onDecode(manualCode)") | Saada
    template(v-else-if="status === 'LOADING'")
      .has-text-centered(): i.fa.fa-2x.fa-cog.fa-spin
    template(v-else-if="status === 'PRE_ASK_ACCEPTANCE'")
      h1.title Tiim: {{ ticket.name }}
      h2.sub-title Mäng: {{ offering.name }}
      .notification.is-warning(v-if="cert.timesUsed >= getMaxUseCount(cert)")
        | Seda koodi on juba kasutatud {{ cert.timesUsed }}/{{ getMaxUseCount(cert) }} korda
      .notification.is-info
        .content
          h3.subtitle Selle pileti koode on kasutatud alljärgnev arv kordi
          ul
            li(v-for="cert in certs")
              strong {{ cert.code }}
              template(v-if="cert.memberId == null"): i (fallback)
              template(v-if="cert.code === certCode"): i (praegune)
              | : {{ cert.timesUsed }}/{{ getMaxUseCount(cert) }}
      .notification.is-info(v-if="isFallback")
        | Tegemist on nö. fallback koodiga.
      button.button.is-success(@click="onAskAcceptance") Küsi kodukorra nõusolekut
    template(v-else-if="status === 'WAIT_ACCEPTANCE'")
      h1.title Tiim: {{ ticket.name }}
      h2.sub-title Mäng: {{ offering.name }}
      .notification.is-info Ootan, et klient nõustuks kodukorraga
      button.button.is-warning(@click="onAcceptance") Nõustus allkirjaga paberil
    template(v-else-if="status === 'GIVE_TICKET'")
      h1.title Tiim: {{ ticket.name }}
      h2.sub-title Mäng: {{ offering.name }}
      .notification.is-info
        | Anna kliendile käepael ning
        strong  veendu
        | , et ta selle sinu nähes ümber käe paneks!
      button.button.is-warn(@click="onTicketGiven") Käepaelaga korras

</template>

<script>
  import {QrcodeStream} from 'vue-qrcode-reader';
  import Vue from 'vue';

  const SCAN = 'SCAN';
  const LOADING = 'LOADING';
  const PRE_ASK_ACCEPTANCE = 'PRE_ASK_ACCEPTANCE';
  const WAIT_ACCEPTANCE = 'WAIT_ACCEPTANCE';
  const GIVE_TICKET = 'GIVE_TICKET';

  export default {
    components: { QrcodeStream },
    name: 'CertAdmin',
    data () {
      return {
        status: LOADING,
        error: null,
        types: null,
        offerings: null,
        ticket: null,
        certCode: null,
        certs: null,
        evtSrc: null,
        manualCode: ''
      };
    },
    methods: {
      onDecode: function(certCode) {
        if (certCode == null) {
          return;
        }
        console.log('Scanned ' + certCode);
        const self = this;
        self.status = LOADING;
        self.certCode = certCode;
        Vue.http.get('api/certs/' + certCode + '/ticket').then(res => {
          self.ticket = res.body;
          if (self.ticket == null) {
            self.reset('Invalid code ' + certCode);
            return;
          }
          return Vue.http.get('api/ticket/' + self.ticket.id + '/certs').then(res => {
            self.certs = res.body;
            self.status = PRE_ASK_ACCEPTANCE;
          });
        }).catch(err => this.reset(err.message));
      },
      onInit (promise) {
        const self = this;
        promise.then(console.log).catch(error => {
          if (error.name === 'NotAllowedError') {
            self.reset('ERROR: you need to grant camera access permisson');
          } else if (error.name === 'NotFoundError') {
            self.reset('ERROR: no camera on this device');
          } else if (error.name === 'NotSupportedError') {
            self.reset('ERROR: secure context required (HTTPS, localhost)');
          } else if (error.name === 'NotReadableError') {
            self.reset('ERROR: is the camera already in use?');
          } else if (error.name === 'OverconstrainedError') {
            self.reset('ERROR: installed cameras are not suitable');
          } else if (error.name === 'StreamApiNotSupportedError') {
            self.reset('ERROR: Stream API is not supported in this browser');
          }
        });
      },
      onAskAcceptance: function() {
        const self = this;
        self.status = LOADING;
        const evtSource = new EventSource(self.$config.apiBase + '/api/certs/' + this.certCode + '/cashierEvents');
        evtSource.addEventListener('EVENT', (e) => {
          console.log(e);
          const event = JSON.parse(e.data);
          if (event === 'GIVE_ACCEPTANCE') {
            self.onAcceptance();
          }
        });
        Vue.http.post('api/certs/' + self.certCode + '/askAcceptance').then(res => {
          console.log(res);
          self.status = WAIT_ACCEPTANCE;
        }).catch(err => self.reset(err.message));
      },
      onAcceptance: function() {
        const self = this;
        self.status = LOADING;
        Vue.http.post('api/certs/' + self.certCode + '/use').then(res => {
          self.status = GIVE_TICKET;
        }).catch(err => this.reset(err.message));
      },
      onTicketGiven: function() {
        this.reset();
      },
      reset: function(error) {
        this.status = SCAN;
        this.ticket = null;
        this.certCode = null;
        this.certs = null;
        if (this.evtSrc != null) {
          this.evtSrc.close();
        }
        this.manualCode = '';
        this.evtSrc = null;
        this.error = error;
      },
      getMaxUseCount: function(cert) {
        if (cert.memberId != null) {
          return 1;
        }
        return this.type.teamSize - this.certs.filter(it => it.memberId != null).length;
      },
      getMember: function(cert) {
        if (cert.memberId != null) {
          return null;
        }
        return this.ticket.members.find(it => it.id === cert.memberId);
      }
    },
    mounted: function () {
      const self = this;
      Promise.all([self.$ticket.getAllOfferings(), self.$ticket.getTypes()]).then(([offerings, types]) => {
        self.offerings = offerings;
        self.types = types;
        this.reset();
      }).catch(ex => this.reset(ex.message));
    },
    computed: {
      isFallback: function() {
        return this.cert != null && this.cert.memberId == null;
      },
      cert: function() {
        return this.certs.find(it => it.code === this.certCode);
      },
      type: function () {
        return this.types.find(type => type.id === this.ticket.typeId);
      },
      offering: function () {
        return this.offerings.find(offering => offering.id === this.ticket.offeringId);
      }
    }
  };
</script>
<style>
  .table-component__filter__clear, .table-component__table__caption, .stats-table-wrapper .table-component__filter {
    display: none !important;
  }
</style>
