<template lang="pug">
  .container
    h1.text-center(v-t="'faq.faqLong'")
    .faqs(v-html="document")
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
