<template lang="pug">
  .columns.is-multiline.is-centered.is-vcentered
    sponsor.column(:class="sizeClasses" :name="name" v-for="name in names" :key="name")
</template>
<script>
  import Sponsor from './Sponsor';

  export default {
    name: 'SponsorGrid',
    components: { Sponsor },
    props: {
      names: Array,
      big: Boolean
    },
    computed: {
      sizeClasses: function() {
        return this.big ? 'is-half is-one-third-desktop' : 'is-one-third is-one-fifth-desktop';
      }
    }
  };
</script>
