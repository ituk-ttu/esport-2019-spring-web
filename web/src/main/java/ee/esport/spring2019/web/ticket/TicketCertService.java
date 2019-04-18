package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.web.email.EmailService;
import ee.esport.spring2019.web.ticket.domain.Ticket;
import ee.esport.spring2019.web.ticket.domain.TicketCert;
import ee.esport.spring2019.web.ticket.domain.TicketType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketCertService {

    @Resource
    private final TicketRepository ticketRepository;

    @Resource
    private final EmailService emailService;

    @Resource
    private final TicketService ticketService;

    public List<TicketCert> generateCerts(int ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);
        TicketType type = ticketService.getType(ticket.getTypeId());
        return null;
    }

}
