<template lang="pug">
  .modal.is-active
    .modal-background
    .modal-card
      .modal-card-head
        .modal-card-title
          h2.title(v-t="'tickets.members.title'")
          h3.subtitle {{ ticket.name }} ({{ offering.name }})
        button.delete(@click="onClose")
      .modal-card-body
        table.table.is-fullwidth
          thead
            tr
              th(v-t="'tickets.members.email'")
              td
                div.has-text-right.has-text-grey {{ ticket.members.length }}/{{ type.teamSize }}
          tbody
            member-item(v-for="member in ticket.members" :member="member" :key="member.id"
                        @delete="onDelete(member)" @save="onUpdate")
            member-edit(v-if="isAdding" @cancel="hideAdd" @save="onAdd")
            tr(v-else-if="canAdd")
              td
              td
                .buttons.is-right
                  button.button.is-success(@click="showAdd")
                    span.icon
                      i.fa.fa-plus
                    span(v-t="'tickets.members.add'")

</template>

<script>
  import MemberItem from './MemberItem';
  import MemberEdit from './MemberEdit';

  export default {
    components: { MemberEdit, MemberItem },
    name: 'MemberManagementModal',
    props: ['ticket', 'offering', 'type'],
    data() {
      return {
        isAdding: false
      };
    },
    computed: {
      canAdd: function() {
        return this.ticket.members.length < this.type.teamSize; // FIXME
      }
    },
    methods: {
      onClose: function() {
        this.$emit('close');
      },
      showAdd: function() {
        this.isAdding = true;
      },
      hideAdd: function() {
      },
      onAdd: function(member) {
        const self = this;
        this.isAdding = false;
        this.$ticket.addMember(this.ticket, member).then(() => {
          self.$forceUpdate();
        });
      },
      onUpdate: function(member) {
        const self = this;
        this.$ticket.updateMember(this.ticket, member).then(() => {
          self.$forceUpdate();
        });
      },
      onDelete: function(member) {
        const self = this;
        this.$ticket.removeMember(this.ticket, member).then(() => {
          self.$forceUpdate();
        });
      }
    }
  };
</script>
<style>
  .table {
    vertical-align: baseline;
  }
</style>
