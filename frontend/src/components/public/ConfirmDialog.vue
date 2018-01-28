<template lang="pug">
  .modal(v-if="show")
    .modal-dialog
      .modal-content
        .modal-header
          button(type="button" class="close" data-dismiss="modal" v-on:click="onCancel()") &times;
          h4.modal-title {{ title }}
        .modal-body
          slot
        .modal-footer
          button.btn.btn-success(type="button" data-dismiss="modal" v-on:click="onConfirm()") {{ confirmText }}
          button.btn.btn-danger(type="button" data-dismiss="modal" v-on:click="onCancel()") {{ cancelText }}
</template>

<script>
  export default {
    name: 'ConfirmDialog',
    props: {
      show: {
        type: Boolean,
        twoWay: true,
        default: false
      },
      title: {
        type: String,
        default: function () { return this.$t('confirmDialog.title'); }
      },
      confirmText: {
        type: String,
        default: function () { return this.$t('confirmDialog.confirm'); }
      },
      cancelText: {
        type: String,
        default: function () { return this.$t('confirmDialog.cancel'); }
      },
      onConfirm: {
        type: Function,
        required: true
      },
      onCancel: {
        type: Function,
        default: function () {}
      }
    },
    methods: {
      close: function () {
        this.show = false;
      }
    },
    mounted: function () {
    },
    data: () => ({
      disabled: true,
      loginUrl: null
    })
  };
</script>
