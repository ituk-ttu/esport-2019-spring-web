<template lang="pug">
  section.section: .container
    h1.has-text-primary.has-text-centered.title.is-1(v-t="'navbar.houseRules'")
    .content.md-content(v-html="document")
</template>

<script>
  export default {
    name: 'HouseRules',
    data () {
      return {
        document: null
      };
    },
    methods: {
      loadRules: function () {
        this.$document.getDocument('houseRules', this.$root.$i18n.locale).then(doc => { this.document = doc; });
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

<style>
  li {
    margin: 10px 0;
  }
</style>
