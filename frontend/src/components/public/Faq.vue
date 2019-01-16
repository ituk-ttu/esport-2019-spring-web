<template lang="pug">
  section.section: .container
    h1.has-text-primary.has-text-centered.title.is-1(v-t="'faq.faqLong'")
    .content.md-content(v-html="document")
</template>

<script>
  export default {
    name: 'Faq',
    data () {
      return {
        document: null
      };
    },
    methods: {
      loadFaq: function () {
        this.$document.getDocument('faq', this.$root.$i18n.locale).then(doc => { this.document = doc; });
      }
    },
    mounted: function () {
      this.loadFaq();
    },
    beforeUpdate: function () {
      this.loadFaq();
    }
  };
</script>
