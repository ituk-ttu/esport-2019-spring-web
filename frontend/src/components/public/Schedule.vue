<template lang="pug">
  .container
    h1.text-center(v-t="'navbar.timetable'")
    .timetable(v-html="document")
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
