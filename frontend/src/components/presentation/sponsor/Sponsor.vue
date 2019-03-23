<template lang="pug">
  a.sponsor.has-text-centered(target="_blank" :href="url")
    img.img-responsive(:src="'../../../static/sponsors/' + image" :style="'transform: scale(' + scale + ');'")
</template>
<script>
  export default {
    name: 'Sponsor',
    props: {
      name: String
    },
    data: function() {
      return {
        sponsors: {
          ituk: {
            url: 'https://ituk.ee',
            image: 'ituk.svg',
            scale: 1.0
          },
          itt: {
            url: 'https://ttu.ee/itt',
            image: 'itt.png',
            scale: 0.9
          },
          ye: {
            url: 'https://ttu.ee/ye',
            image: 'ye.png',
            scale: 0.85
          },
          arvutitark: {
            url: 'https://arvutitark.ee/',
            image: 'arvutitark_logo.png',
            scale: 0.85
          },
          lg: {
            url: 'https://www.lg.com/ee',
            image: 'LG-new.svg',
            scale: 0.7
          },
          filmiklubi: {
            url: 'https://filmiklubi.ee',
            image: 'filmiklubi-03.svg',
            scale: 0.9
          },
          thorgate: {
            url: 'https://thorgate.eu',
            image: 'thorgate.png',
            scale: 0.9
          },
          belief: {
            url: 'https://www.beliefwater.com/',
            image: 'Belief.png',
            scale: 1.0
          },
          networkTomorrow: {
            url: 'http://www.tomorrow.ee',
            image: 'nt.png    ',
            scale: 1.05
          }
        }
      };
    },
    computed: {
      url: function() {
        return this.sponsors[this.name].url;
      },
      image: function() {
        return this.sponsors[this.name].image;
      },
      scale: function() {
        return this.sponsors[this.name].scale;
      }
    }
  };
</script>
