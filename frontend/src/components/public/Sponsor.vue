<template lang="pug">
  a.sponsor.column.is-full-mobile.is-full-tablet.is-half-desktop.is-one-third-widescreen.is-one-third-fullhd.has-text-centered(v-if="big" target="_blank" :href="url")
    img.img-responsive(:src="'../../../static/sponsors/' + image")
  a.sponsor.column.is-half-mobile.is-one-third-tablet.is-one-third-desktop.is-one-quarter-widescreen.is-one-fifth-fullhd.has-text-centered(v-else target="_blank" :href="url")
    img.img-responsive(:src="'../../../static/sponsors/' + image")
</template>
<script>
  export default {
    name: 'Sponsor',
    props: {
      image: String,
      url: String,
      big: Boolean
    }
  };
</script>
