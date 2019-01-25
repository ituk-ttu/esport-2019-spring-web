package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.web.email.EmailService;
import ee.esport.spring2019.web.ticket.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    @Resource
    private final TicketRepository ticketRepository;

    @Resource
    private final EmailService emailService;

    public List<TicketType> getAllTypes() {
        return ticketRepository.getAllTypes();
    }

    public TicketType getType(int typeId) {
        return ticketRepository.getType(typeId);
    }

    // No more than one per type, active or next to be active
    public List<TicketOffering> getVisibleOfferings() {
        return ticketRepository.getAllOfferings()
                               .stream()
                               .collect(Collectors.groupingBy(TicketOffering::getTypeId))
                               .values()
                               .stream()
                               .map(this::findVisibleOffering)
                               .filter(Objects::nonNull)
                               .collect(Collectors.toList());
    }

    private TicketOffering findVisibleOffering(List<TicketOffering> offerings) {
        Optional<TicketOffering> activeOffering = offerings.stream()
                                                           .filter(this::isActive)
                                                           .findAny();
        if (activeOffering.isPresent()) {
            return activeOffering.get();
        }
        Optional<TicketOffering> nextActiveOffering =
                offerings.stream()
                         .filter(offering -> !isExpired(offering))
                         .min(Comparator.comparing(TicketOffering::getAvailableFrom));
        return nextActiveOffering.orElse(null);
    }

    private boolean isActive(TicketOffering offering) {
        return !isNotYetActive(offering) && !isExpired(offering);
    }

    private boolean isExpired(TicketOffering offering) {
        return offering.getAvailableUntil() != null && offering.getAvailableUntil().isBefore(OffsetDateTime.now());
    }

    private boolean isNotYetActive(TicketOffering offering) {
        return offering.getAvailableFrom() != null && OffsetDateTime.now().isBefore(offering.getAvailableFrom());
    }

    public Ticket createTicket(TicketCreation creation) {
        //TODO: Check if ticket available and remaining
        TicketCandidate candidate = TicketCandidate.builder()
                                               .offeringId(creation.getOfferingId())
                                               .ownerId(creation.getOwnerId())
                                               .seat(creation.getSeat())
                                               .status(Ticket.Status.AWAITING_PAYMENT) //FIXME: or waiting list?
                                               .build();
        Ticket ticket = ticketRepository.createTicket(candidate);
        sendTicketCreationEmail(ticket);
        return ticket;
    }

    //FIXME: loginLink
    @SneakyThrows //FIXME: remove
    private void sendTicketCreationEmail(Ticket ticket) {
        if(ticket.getStatus() == Ticket.Status.AWAITING_PAYMENT) {
            emailService.sendTicketReservation(ticket, "loginLink").get();
        } else {
            emailService.sendTicketWaiting(ticket, "loginLink").get();
        }
    }

    public Ticket getTicket(int ticketId) {
        return ticketRepository.getTicket(ticketId);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.getAllTickets();
    }

    public void cancelTicket(Ticket ticket) {
        throw new NotImplementedException("Ticket cancellation not yet implemented");
    }

    public void confirmTicketPaid(Ticket ticket, String referer) {
        throw new NotImplementedException("Ticket payment confirmation not yet implemented");
    }

    public void deleteMember(int ticketId, int memberId) {
        ticketRepository.deleteMember(ticketId, memberId);
    }

}
