<template lang="pug">
  .container
    h1.text-center(v-t="'faq.faqLong'")
    .faqs
      .faq(v-for="i in 7")
        p: strong(v-t="'faq.questions.' + i + '.q'")
        p(v-t="'faq.questions.' + i + '.a'")
      .faq
        p: strong(v-t="'faq.questions.8.q'")
        p {{ $t('faq.questions.8.a') }}&nbsp
          a(:href="$t('faq.questions.8.file')" target="_blank") {{ $t('faq.questions.8.parentalPermit') }}
          | .
</template>

<script>
  export default {
    name: 'Faq',
    data () {
      return {};
    }
  };
</script>
