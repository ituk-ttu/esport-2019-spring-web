<template lang="pug">
  section.section: .container
    h1.has-text-primary.has-text-centered.title.is-1(v-t="'navbar.timetable'")
    .content.md-content(v-html="document")
</template>

<script>
  export default {
    name: 'Schedule',
    data () {
      return {
        document: null
      };
    },
    methods: {
      loadSchedule: function () {
        this.$document.getDocument('schedule', this.$root.$i18n.locale).then(doc => { this.document = doc; });
      }
    },
    mounted: function () {
      this.loadSchedule();
    },
    beforeUpdate: function () {
      this.loadSchedule();
    }
  };
</script>
