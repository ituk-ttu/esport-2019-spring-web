package ee.esport.spring2019.web.ticket.domain;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class TicketCreation {

    @NotNull private final Integer offeringId;
    @NotNull private final Integer ownerId;
    @NotNull private final String name;
    private final Integer seat;

}
