package ee.esport.spring2019.web.ticket.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class TicketType {

    @NonNull private final Integer id;
    @NonNull private final String code;
    private final Integer amountAvailable;
    private final Integer amountRemaining;
    @NonNull private final Integer teamSize;
    @NonNull private final Boolean assignedSeating;

}
