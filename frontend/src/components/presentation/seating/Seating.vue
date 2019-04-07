<template lang="pug">
    svg(viewBox="0 0 680 480")
      template(v-for="seat in seats")
        rect(:x="seat.x" :y="seat.y" :height="seat.height" :width="seat.width"
             :class="getClass(seat) + ' seat-container'" @click="select(seat)")
        rect(:x="seat.x + strokeWidth" :y="seat.y + strokeWidth"
             :height="seat.height - 2 * strokeWidth" :width="seat.width - 2 * strokeWidth"
             :class="getClass(seat) + ' seat-body'" @click="select(seat)")
        text(:x="seat.x + seat.width / 2" :y="seat.y + seat.height / 2"
             dominant-baseline="central" text-anchor="middle"
             :transform="'rotate(' + seat.rotation + ', ' + (seat.x + seat.width / 2) + ', ' + (seat.y + seat.height / 2) + ')'"
             :class="getClass(seat) + ' seat-title'" @click="select(seat)")
          | {{seat.gameTitle + ' ' + seat.index}}


</template>

<script>

  const pos = (x, y) => ({x, y});

  const cellSize = 40;

  const seatings = [
    {
      game: 'CSGO',
      title: 'CSGO',
      height: 5,
      width: 1,
      rotation: 270,
      seats: [
        pos(0, 2),
        pos(0, 7),
        pos(1, 2),
        pos(1, 7),
        pos(3, 2),
        pos(3, 7),
        pos(4, 2),
        pos(4, 7),
        pos(6, 2),
        pos(6, 7),
        pos(7, 2),
        pos(7, 7),
        pos(9, 2),
        pos(9, 7),
        pos(10, 2),
        pos(10, 7),
        pos(12, 2),
        pos(12, 7),
      ]
    },
    {
      game: 'CSGO_VIP',
      title: 'CSGO VIP',
      height: 1,
      width: 5,
      rotation: 0,
      seats: [
        pos(2, 0),
        pos(10, 0)]
    },
    {
      game: 'REGULAR',
      title: 'R',
      height: 1,
      width: 1,
      rotation: 270,
      seats: [
        pos(13, 2),
        pos(13, 3),
        pos(13, 4),
        pos(13, 5),
        pos(13, 6),
        pos(13, 7),
        pos(13, 8),
        pos(13, 9),
        pos(13, 10),
        pos(13, 11),
        pos(15, 2),
        pos(15, 3),
        pos(15, 4),
        pos(15, 5),
        pos(15, 6),
        pos(15, 7),
        pos(15, 8),
        pos(15, 9),
        pos(15, 10),
        pos(15, 11),
      ]
    },
    {
      game: 'REGULAR_MSI',
      title: 'M',
      height: 1,
      width: 1,
      rotation: 270,
      seats: [
        pos(16, 2),
        pos(16, 3),
        pos(16, 4),
        pos(16, 5),
        pos(16, 6),
        pos(16, 7),
        pos(16, 8),
        pos(16, 9),
        pos(16, 10),
        pos(16, 11),
      ]
    }
  ];

  const statuses = {
    UNAVAILABLE: 'UNAVAILABLE',
    TAKEN: 'TAKEN',
    CURRENT: 'CURRENT',
    AVAILABLE: 'AVAILABLE'
  };

  const classes = {
    UNAVAILABLE: 'seat-unavailable',
    TAKEN: 'seat-taken',
    CURRENT: 'seat-current',
    AVAILABLE: 'seat-available'
  };

  export default {
    name: "Seating",
    props: ['ticket', 'ticketType', 'availableSeats'],
    data: function() {
      return {
        strokeWidth: 3,
        seats: seatings.flatMap(gameSeating => gameSeating.seats.map((seat, index) => ({
          x: seat.x * cellSize,
          y: seat.y * cellSize,
          game: gameSeating.game,
          rotation: gameSeating.rotation,
          gameTitle: gameSeating.title,
          height: gameSeating.height * cellSize,
          width: gameSeating.width * cellSize,
          index,
        })))
      }
    },
    methods: {
      getStatus: function (seat) {
        if (seat.game !== this.ticketType.code) {
          return statuses.UNAVAILABLE;
        }
        if (seat.index === this.ticket.seat) {
          return statuses.CURRENT;
        }
        if (this.availableSeats.some(it => it === seat.index)) {
          return statuses.AVAILABLE;
        }
        return statuses.TAKEN;
      },
      getClass: function(seat) {
        return classes[this.getStatus(seat)];
      },
      select: function (seat) {
        if (this.getStatus(seat) !== statuses.AVAILABLE) {
          return;
        }
        this.$ticket.selectSeat(this.ticket.id, seat.index).then(it => {
          this.ticket.seat = seat.index;
        });
      }
    }
  }
</script>

<style scoped>
svg {
  width: 100%;
}
.seat-container.seat-unavailable {
  fill: #b3b3b3;
}
.seat-container.seat-taken {
  fill: #a8142d;
}
.seat-container.seat-current {
  fill: #45a614;
}
.seat-container.seat-available {
  fill: #1676a6;
}
.seat-available {
  cursor: pointer;
}
.seat-body {
  fill: rgba(255, 255, 255, 0.5)
}
</style>
