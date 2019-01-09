package ee.esport.spring2019.web.ticket;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TicketStatus {

    IN_WAITING_LIST(true),
    AWAITING_PAYMENT(true),
    PAID(true),
    CANCELED(false);

    private final boolean active;

}
