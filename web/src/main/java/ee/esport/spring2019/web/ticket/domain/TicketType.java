package ee.esport.spring2019.web.ticket.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Value
@Builder
public class TicketType {

    @NonNull private final Integer id;
    @NonNull private final String code;
    @NonNull private final Integer amountAvailable;
    @NonNull private final Integer amountActive;
    @NonNull private final Integer teamSize;
    @NonNull private final Boolean assignedSeating;
    @NonNull private final List<Offering> offerings;

    @Value
    @Builder
    public static class Offering {

        @NonNull private final Integer id;
        @NonNull private final String name;
        private final Integer amountAvailable;
        @NonNull private final Integer amountActive;
        private final OffsetDateTime availableFrom;
        private final OffsetDateTime availableUntil;
        @NonNull private final Boolean availableOnline;
        @NonNull private final Boolean promotional;
        @NonNull private final BigDecimal cost;

    }

}
