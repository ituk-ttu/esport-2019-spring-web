<template lang="pug">
  li.dropdown
    a.dropdown-toggle(href="#" data-toggle="dropdown" role="button" aria-expanded="false") {{ title }}&nbsp
      span.caret
    ul.dropdown-menu(role="menu")
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
