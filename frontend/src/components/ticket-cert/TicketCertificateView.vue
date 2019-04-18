<template lang="pug">
  div
    h1.title(v-t="'tickets.certs.redeem.title'")
    p.sub-title(v-t="'tickets.certs.redeem.guide'")
    .qr
      vue-qr(:text="ticketCertCode" dotScale="1.0" margin="0" size="378")
    p.code {{ ticketCertCode }}

</template>

<script>
  import VueQr from 'vue-qr';

  export default {
    name: 'TicketCertificateHome',
    props: ['ticketCertCode'],
    components: { VueQr },
    created () {
      this.$root.$i18n.locale = this.$root.$localStorage.get('language', 'et');
      this.$moment.locale(this.$root.$i18n.t('moment'));
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
