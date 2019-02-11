<template lang="pug">
  section.section: .container
    h1.title.has-text-primary.is-1.has-text-centered(v-t="'contact.contacts'")
    .columns.is-multiline.is-centered
      .column.is-one-third-desktop.is-half-tablet.is-full-mobile
        .card.full-height-card
          .card-image
            figure.image
              img(src="../../assets/images/rasmus.jpg")
          .card-content
            h1.title.has-text-primary Rasmus Rüngenen
            h2.subtitle.has-text-dark.has-text-weight-bold(v-t="'contact.chiefOrganizer'")
            p
              i.fa.fa-envelope.fa-fw
              | &nbsp;
              a(href="mailto:r.ryngenen@gmail.com") r.ryngenen@gmail.com
            p
              i.fa.fa-phone.fa-fw
              | &nbsp;
              a(href="tel:+37259061664") 5906 1664
      .column.is-one-third-desktop.is-half-tablet.is-full-mobile
        .card.full-height-card
          .card-image
            figure.image
              img(src="../../assets/images/ituk_pic.jpg")
          .card-content
            h1.title.has-text-primary(v-t="'contact.ituk'")
            h2.subtitle.has-text-dark.has-text-weight-bold(v-t="'contact.organizers'")
            p
              i.fa.fa-envelope.fa-fw
              | &nbsp;
              a(href="mailto:kontakt@ituk.ee") kontakt@ituk.ee
    gmap-map.contact-map(:center="{lat: 59.3952914, lng: 24.6718043}", :zoom="15")
      gmap-marker(:position="{lat: 59.3952914, lng: 24.6718043}")
</template>

<script>
  export default {
    name: 'Contact',
    data () {
      return {};
    }
  };
</script>
