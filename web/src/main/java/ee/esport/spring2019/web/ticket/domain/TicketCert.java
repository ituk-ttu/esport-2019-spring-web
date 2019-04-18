package ee.esport.spring2019.web.ticket.domain;

import lombok.NonNull;
import lombok.Value;

@Value
public class TicketCert {

    @NonNull private final Integer id;
    @NonNull private final String code;
    private final Integer memberId;

}
