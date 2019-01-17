package ee.esport.spring2018.web.ticket.domain;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class TicketCreation {

    @NotNull private final Integer offeringId;
    @NotNull private final Integer ownerId;
    private final Integer seat;

}
