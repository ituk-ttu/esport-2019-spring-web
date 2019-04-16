<template lang="pug">
  tr
    td
      .field
        .control
          input.input(v-model="value.email")
    td
      .buttons.is-right
        button.button.is-success(@click="onSave")
          span.icon
            i.fa.fa-check
        button.button.is-danger(@click="onCancel")
          span.icon
            i.fa.fa-times

</template>

<script>
  export default {
    name: 'MemberEdit',
    props: ['member'],
    data() {
      return {
        value: null
      };
    },
    methods: {
      onSave: function() {
        this.$emit('save', this.value);
      },
      onCancel: function () {
        this.$emit('cancel');
      }
    },
    created: function () {
      this.value = this.member != null ? { ...this.member } : { email: '' };
    }
  };
</script>
