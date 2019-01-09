package ee.esport.spring2019.web.ticket;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class Ticket {

    private Integer id;
    private TicketType type;
    private String name;

    @Email
    private String ownerEmail;
    private String ownerSteamId;
    private TicketStatus status;
    private OffsetDateTime dateCreated;
    private List<TicketMember> members;

}
