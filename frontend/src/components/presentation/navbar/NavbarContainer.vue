<template lang="pug">
  .navbar.navbar-default.navbar-static-top
    .container
      .navbar-header
        button.navbar-toggle.collapsed(type="button" data-toggle="collapse" data-target=".navbar-collapse")
          span.sr-only Toggle navigation
          span.icon-bar
          span.icon-bar
          span.icon-bar
        router-link.navbar-brand(to="/")
          slot(name="logo")
        .collapse.navbar-collapse
          ul.nav.navbar-nav.navbar-right
            slot
</template>

<script>
  export default {
    name: 'navbar-container'
  };
</script>
