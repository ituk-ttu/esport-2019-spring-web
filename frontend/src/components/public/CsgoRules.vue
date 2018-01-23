<template lang="pug">
  .container
    h1.text-center(v-t="'navbar.csgoRules'")
    .csgo-rules(v-html="document")
</template>

<script>
  export default {
    name: 'CsgoRules',
    data () {
      return {
        document: null
      };
    },
    mounted: function () {
      this.$document.getDocument('csgoRules', this.$root.$i18n.locale).then(doc => { this.document = doc; });
    }
  };
</script>

<style>
  li {
    margin: 10px 0;
  }
</style>
