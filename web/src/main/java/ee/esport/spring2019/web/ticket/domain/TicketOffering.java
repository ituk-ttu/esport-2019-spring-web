package ee.esport.spring2019.web.ticket.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Value
@Builder
public class TicketOffering {

    @NonNull private final Integer id;
    @NonNull private final String name;
    @NonNull private final Integer typeId;
    private final Integer amountAvailable;
    @NonNull private final Integer amountRemaining;
    private final OffsetDateTime availableFrom;
    private final OffsetDateTime availableUntil;
    @NonNull private final Boolean availableOnline;
    @NonNull private final Boolean promotional;
    @NonNull private final BigDecimal cost;

}
