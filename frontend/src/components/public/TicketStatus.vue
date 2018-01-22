<template lang="pug">
  span.label(:class="getStatusClass(status)")
    small {{ $t('tickets.statuses["' + status + '"]') }}
</template>

<script>
  export default {
    name: 'TicketStatus',
    props: ['status'],
    methods: {
      getStatusClass: function (status) {
        switch (status) {
          case 'IN_WAITING_LIST':
            return 'label-warning';
          case 'AWAITING_PAYMENT':
            return 'label-info';
          case 'PAID':
            return 'label-success';
          case 'CANCELED':
            return 'label-danger';
          default:
            return 'label-primary';
        }
      }
    }
  };
</script>
