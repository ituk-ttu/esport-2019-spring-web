package ee.esport.spring2019.web.ticket.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;

@Value
@Builder
public class Ticket {

    @NonNull private final Integer id;
    @NonNull private final Integer typeId;
    @NonNull private final String name;
    @NonNull private final Integer offeringId;
    @NonNull private final Integer ownerId;
    private final Integer seat;
    @NonNull private final Status status;
    @NonNull private final OffsetDateTime dateCreated;
    @NonNull private final List<Ticket.Member> members;

    @Value
    @Builder
    public static class Member {

        @NonNull private final Integer id;
        @NonNull private final String igName;
        @NonNull private final String email;

    }

    public enum Status {

        IN_WAITING_LIST,
        AWAITING_PAYMENT,
        PAID,
        CANCELED

    }

}
