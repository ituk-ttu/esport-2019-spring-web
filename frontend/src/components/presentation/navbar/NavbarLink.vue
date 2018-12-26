<template lang="pug">
  li
    router-link(v-if="getType() === LINK_LOCAL" :class="getStyleClass()"  v-on:click="onClick()"
                :to="{ name: targetPage }") {{ title }}
    a(v-if="getType() === LINK_EXTERNAL" :class="getStyleClass()" v-on:click="onClick()"
      :href="targetUrl" :target="newWindow ? '_blank' : null") {{ title }}
    a(v-if="getType() === LINK_VOID" :class="getStyleClass()"
      href="#" @click.prevent="onClick()") {{ title }}
</template>

<script>
  const LINK_LOCAL = 'LINK_LOCAL';
  const LINK_EXTERNAL = 'LINK_EXTERNAL';
  const LINK_VOID = 'LINK_VOID';

  const STYLE_CLASSES = {
    'bold': 'link-bold',
    'discord': 'link-discord'
  };

  export default {
    name: 'navbar-link',
    props: {
      title: {
        type: String,
        required: true
      },
      targetPage: String,
      targetUrl: String,
      newWindow: {
        type: Boolean,
        default: false
      },
      look: String
    },
    methods: {
      getType () {
        return this.targetPage != null ? LINK_LOCAL :
               this.targetUrl != null ? LINK_EXTERNAL :
               LINK_VOID;
      },
      getStyleClass () {
        if (this.look == null) {
          return null;
        }
        const styleClass = STYLE_CLASSES[this.look];
        if (styleClass == null) {
          throw new Error(`Unknown <NavbarLink> style "${this.look}"`);
        }
        return styleClass;
      },
      onClick () {
        this.$emit('go');
      }
    },
    data () {
      return {
        LINK_LOCAL,
        LINK_EXTERNAL,
        LINK_VOID
      };
    }
  };
</script>

<style lang="less">
  .link-bold {
    font-weight: bold;
  }
  .link-discord {
    font-weight: bold;
    color: #7289DA;
    &:hover, &:active {
      color: #99AAB5;
    }
  }
</style>
