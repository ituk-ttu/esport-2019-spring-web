<template lang="pug">
  div
    router-view.view(v-if="isAdmin()")
    .container(v-else)
      .alert.alert-danger(role="alert") {{ $t('admin.general.notAuthorized') }}
</template>

<script>
  export default {
    name: 'Admin',
    methods: {
      isAdmin: function () {
        return this.$auth.isAdmin();
      }
    }
  };
</script>
