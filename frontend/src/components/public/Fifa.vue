<template lang="pug">
  section.section: .container
    h1.has-text-centered.has-text-primary.title.is-1(v-t="'navbar.fifa'")
    .content.md-content(v-html="document")
</template>

<script>
  export default {
    name: 'Fifa',
    data () {
      return {
        document: null
      };
    },
    methods: {
      loadRules: function () {
        this.$document.getDocument('fifa', this.$root.$i18n.locale).then(doc => { this.document = doc; });
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

