package ee.esport.spring2019.web.ticket.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class TicketCert {

    @NonNull private final Integer id;
    @NonNull private final String code;
    private final Integer memberId;
    @NonNull private final Integer timesUsed;

}
