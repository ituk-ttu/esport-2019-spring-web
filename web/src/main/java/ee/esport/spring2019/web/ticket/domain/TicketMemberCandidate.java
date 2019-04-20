package ee.esport.spring2019.web.ticket.domain;

import lombok.NonNull;
import lombok.Value;

@Value
public class TicketMemberCandidate {

    @NonNull private final String email;

}
