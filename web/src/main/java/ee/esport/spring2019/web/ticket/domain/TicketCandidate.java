package ee.esport.spring2019.web.ticket.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class TicketCandidate {

    @NonNull private final Integer offeringId;
    @NonNull private final Integer ownerId;
    @NonNull private final String name;
    private final Integer seat;
    @NonNull private final Ticket.Status status;

}
