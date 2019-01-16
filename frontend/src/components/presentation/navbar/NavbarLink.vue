<template lang="pug">
    router-link(v-if="getType() === LINK_LOCAL" :class="getStyleClass()"  v-on:click="onClick()"
                :to="{ name: targetPage }").navbar-item {{ title }}
    a(v-else-if="getType() === LINK_EXTERNAL" :class="getStyleClass()" v-on:click="onClick()"
      :href="targetUrl" :target="newWindow ? '_blank' : null").navbar-item {{ title }}
    a(v-else-if="getType() === LINK_VOID" :class="getStyleClass()"
      href="#" @click.prevent="onClick()").navbar-item {{ title }}
</template>

<script>
  const LINK_LOCAL = 'LINK_LOCAL';
  const LINK_EXTERNAL = 'LINK_EXTERNAL';
  const LINK_VOID = 'LINK_VOID';

  const STYLE_CLASSES = {
    'bold': 'has-text-weight-bold',
    'discord': 'link-discord',
    'default': 'has-text-weight-semibold'
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
          return STYLE_CLASSES['default'];
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
