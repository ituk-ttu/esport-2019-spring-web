<template lang="pug">
  section.section: .container
    h1.has-text-centered.has-text-primary.title.is-1(v-t="'navbar.csgoRules'")
    .content.md-content(v-html="document")
</template>

<script>
  export default {
    name: 'CsgoRules',
    data () {
      return {
        document: null
      };
    },
    methods: {
      loadRules: function () {
        this.$document.getDocument('csgoRules', this.$root.$i18n.locale).then(doc => { this.document = doc; });
      }
    },
    mounted: function () {
      this.loadRules();
    },
    beforeUpdate: function () {
      this.loadRules();
    }
  };
</script>

