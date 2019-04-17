<template lang="pug">
  member-edit(v-if="isEditing" :member="member" @save="finishEdit" @cancel="cancelEdit")
  tr(v-else)
    template
      td {{ member.email }}
      td
        .buttons.is-right
          button.button.is-success(@click="startEdit")
            span.icon
              i.fa.fa-edit
          button.button.is-danger(@click="onDelete")
            span.icon
              i.fa.fa-times

</template>

<script>
  import MemberEdit from './MemberEdit';

  export default {
    name: 'MemberItem',
    components: { MemberEdit },
    props: ['member'],
    data() {
      return {
        isEditing: false
      };
    },
    methods: {
      onDelete: function() {
        // TODO Confirm?
        this.$emit('delete');
      },
      startEdit: function() {
        this.isEditing = true;
      },
      cancelEdit: function () {
        this.isEditing = false;
      },
      finishEdit: function(newValue) {
        this.isEditing = false;
        this.$emit('save', newValue);
      }
    }
  };
</script>
