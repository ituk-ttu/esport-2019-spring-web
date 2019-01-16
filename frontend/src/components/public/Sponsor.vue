<template lang="pug">
  a.sponsor.column.is-half.has-text-centered(v-if="big" target="_blank" :href="url")
    img.img-responsive(:src="'../../../static/sponsors/' + image")
  a.sponsor.column.is-one-quarter-mobile.is-one-third.has-text-centered(v-else target="_blank" :href="url")
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
