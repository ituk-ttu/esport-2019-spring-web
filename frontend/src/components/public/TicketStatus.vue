<template lang="pug">
  span.tag(:class="getStatusClass(status)") {{ $t('tickets.statuses["' + status + '"]') }}
</template>

<script>
  export default {
    name: 'TicketStatus',
    props: ['status'],
    methods: {
      getStatusClass: function (status) {
        switch (status) {
          case 'IN_WAITING_LIST':
            return 'is-warning';
          case 'AWAITING_PAYMENT':
            return 'is-info';
          case 'PAID':
            return 'is-success';
          case 'CANCELED':
            return 'is-danger';
          default:
            return 'is-primary';
        }
      }
    }
  };
</script>
