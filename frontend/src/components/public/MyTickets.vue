<template lang="pug">
  div
    .container(v-if="!$parent.isAdmin()")
      h1.text-center(v-t="'tickets.tickets'")
      blockquote.my-ticket(v-for="ticket in tickets")
        p.lead {{ $t('buy.name') }}:
          span.text-primary  {{ticket.name}}
        p: small.text-default {{ $t('buy.invoiceNumber') }}:
          span.text-primary  {{getInvoiceNumber(ticket.id)}}
        p: small.text-default {{ $t('buy.ticket') }}:
          span.text-primary  {{ $t('tickets.names["' + ticket.type.name + '"]') }}
        p: small.text-default {{ $t('tickets.status') }}:&nbsp
          | {{ $t('tickets.statuses["' + ticket.status + '"]') }}
        p: small.text-default {{ $t('tickets.boughtOnDate') }}:
          span.text-primary  {{ticket.dateCreated | moment("Do MMMM HH:mm") }}
        p(v-if="canConfirmTicket(ticket)")
          button.btn.btn-success(v-on:click="confirmTicket(ticket)") {{ $t('tickets.confirm') }}
        p(v-if="canCancelTicket(ticket)")
          button.btn.btn-danger(v-on:click="cancelTicket(ticket)") {{ $t('tickets.cancel') }}
        p {{ $t('buy.total') }}:
          span.text-primary  {{ticket.type.cost}} €
    .container(v-else)
      table-component(:data="tickets" sort-by="dateCreated" sort-order="desc" table-class="table"
                      filter-input-class="form-control" show-caption="false" filter-no-results="¯\\_(ツ)_/¯")
        table-column(show="id" label="ID" data-type="numeric")
        table-column(show="name" label="Name")
        table-column(show="type.name" label="Type")
        table-column(show="ownerEmail" label="Owner Email")
        table-column(show="type.cost" label="Cost" data-type="numeric")
        table-column(show="status" label="Status")
          template(slot-scope="row")
            span.label(:class="getStatusClass(row.status)")
              small {{ $t('tickets.statuses["' + row.status + '"]') }}
        table-column(show="createdAt" label="Bought on")
          template(slot-scope="row") {{row.dateCreated | moment("Do MMMM HH:mm") }}
        table-column(label="", :sortable="false", :filterable="false")
          template(slot-scope="row")
            span(v-if="canConfirmTicket(row)")
              button.btn.btn-success.btn-sm(v-on:click="confirmTicket(row)") {{ $t('tickets.confirm') }}
        table-column(label="", :sortable="false", :filterable="false")
          template(slot-scope="row")
            span(v-if="canCancelTicket(row)")
              button.btn.btn-danger.btn-sm(v-on:click="cancelTicket(row)") {{ $t('tickets.cancel') }}

</template>

<script>
  export default {
    name: 'MyTickets',
    data () {
      return {
        tickets: [],
        columns: [
          {
            title: 'id'
          }
        ]
      };
    },
    methods: {
      getInvoiceNumber: function (id) {
        return '2018-359027-' + '000'.substring(0, 3 - id.toString().length) + id.toString();
      },
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
      },
      canConfirmTicket: function (ticket) {
        return this.$ticket.canConfirm(ticket);
      },
      canCancelTicket: function (ticket) {
        return this.$ticket.canCancel(ticket);
      },
      confirmTicket: function (ticket) {
        this.$ticket.confirm(ticket).then(res => { ticket.status = 'PAID'; });
      },
      cancelTicket: function (ticket) {
        this.$ticket.cancel(ticket).then(res => { ticket.status = 'CANCELED'; });
      }
    },
    created: function () {
      if (!this.$auth.isLoggedIn()) {
        this.$router.push({ name: 'Login' });
      }
    },
    mounted: function () {
      const self = this;
      self.$ticket.getTickets().then(tickets => { self.tickets = tickets; });
    }
  };
</script>
<style>
  .table-component__filter__clear, .table-component__table__caption {
    display: none !important;
  }
</style>
