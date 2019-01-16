<template lang="pug">
  .navbar-item.has-dropdown.is-hoverable
    a.navbar-link.has-text-weight-semibold {{ title }}&nbsp
      span.caret
    .navbar-dropdown(role="menu")
      slot
</template>

<script>
  export default {
    name: 'navbar-dropdown',
    props: {
      title: {
        type: String,
        required: true
      }
    }
  };
</script>
