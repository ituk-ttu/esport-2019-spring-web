package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.web.auth.user.UserService;
import ee.esport.spring2019.web.email.EmailService;
import ee.esport.spring2019.web.ticket.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    @Resource
    private final TicketRepository ticketRepository;

    @Resource
    private final EmailService emailService;

    @Resource
    private final UserService userService;

    public List<TicketType> getAllTypes() {
        return ticketRepository.getAllTypes();
    }

    public TicketType getType(int typeId) {
        return ticketRepository.getType(typeId);
    }

    public TicketOffering getVisibleOffering(int id) {
        return getVisibleOfferings().stream()
                                    .filter(it -> it.getId() == id)
                                    .findAny()
                                    .orElseThrow(() -> new NoSuchElementException("Ticket offering not visible or" +
                                                                                  "does not exist"));
    }

    public List<TicketOffering> getAllOfferings() {
        return ticketRepository.getAllOfferings();
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

    private boolean isNotYetActive(TicketOffering offering) {
        return offering.getAvailableFrom().isAfter(OffsetDateTime.now());
    }

    private boolean isExpired(TicketOffering offering) {
        return !offering.getAvailableUntil().isAfter(OffsetDateTime.now());
    }

    @Transactional
    public Ticket createTicket(TicketCreation creation) {
        TicketOffering offering = ticketRepository.getOffering(creation.getOfferingId());
        if (!isActive(offering)) {
            throw new IllegalArgumentException("Ticket offering not active");
        }
        if (!offering.getAvailableOnline()) {
            throw new IllegalArgumentException("Ticket offering not available online");
        }
        Ticket.Status status = offering.getAmountRemaining() > 0 ?
                               Ticket.Status.AWAITING_PAYMENT :
                               Ticket.Status.IN_WAITING_LIST;
        TicketCandidate candidate = TicketCandidate.builder()
                                                   .offeringId(creation.getOfferingId())
                                                   .ownerId(creation.getOwnerId())
                                                   .seat(creation.getSeat())
                                                   .status(status)
                                                   .name(creation.getName())
                                                   .build();
        Ticket tempTicket = ticketRepository.createTicket(candidate);
        String ownerEmail = userService.getUserEmail(tempTicket.getOwnerId());
        ticketRepository.addMember(tempTicket.getId(), new TicketMemberCandidate(ownerEmail));
        Ticket ticket = ticketRepository.getTicket(tempTicket.getId());
        sendTicketCreationEmail(ticket);
        return ticket;
    }

    @SneakyThrows //FIXME: remove
    public void sendTicketCreationEmail(Ticket ticket) {
        if(ticket.getStatus() == Ticket.Status.AWAITING_PAYMENT) {
            emailService.sendEmail("ticketReserved", ticket, getType(ticket.getTypeId()),
                                   ticketRepository.getOffering(ticket.getOfferingId()))
                        .get();
        } else {
            emailService.sendEmail("ticketWaiting", ticket, getType(ticket.getTypeId()),
                                   ticketRepository.getOffering(ticket.getOfferingId()))
                        .get();
        }
    }

    public Ticket getTicket(int ticketId) {
        return ticketRepository.getTicket(ticketId);
    }

    public List<Ticket> getUserTickets(int userId) {
        return ticketRepository.getUserTickets(userId);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.getAllTickets();
    }

    public void cancelTicket(Ticket ticket) {
        ticketRepository.cancelTicket(ticket);
        emailService.sendEmail("ticketCanceled", ticket, getType(ticket.getTypeId()),
                               ticketRepository.getOffering(ticket.getOfferingId()));
    }

    public void confirmTicketPaid(Ticket ticket) {
        ticketRepository.confirmTicketPaid(ticket);
        emailService.sendEmail("ticketConfirmed", ticket, getType(ticket.getTypeId()),
                               ticketRepository.getOffering(ticket.getOfferingId()));
    }

    public Ticket.Member updateMember(int ticketId, int memberId, TicketMemberCandidate member) {
        return ticketRepository.updateMember(ticketId, memberId, member);
    }

    public Ticket.Member addMember(int ticketId, TicketMemberCandidate member) {
        Ticket ticket = ticketRepository.getTicket(ticketId);
        TicketType type = ticketRepository.getType(ticket.getTypeId());
        if (type.getTeamSize() <= ticket.getMembers().size()) {
            throw new IllegalStateException("Ticket already has maximum amount of members");
        }
        return ticketRepository.addMember(ticketId, member);
    }

    public void deleteMember(int ticketId, int memberId) {
        ticketRepository.deleteMember(ticketId, memberId);
    }

    public TicketOffering getFromAllOfferings(int id) {
        return getAllOfferings().stream()
                .filter(it -> it.getId() == id)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Ticket offering does not exist"));
    }

    public Map<Integer, String> getOwnerEmails() {
        return getAllTickets().stream()
                              .collect(Collectors.toMap(Ticket::getId,
                                                        ticket -> userService.getUserEmail(ticket.getOwnerId()),
                                                        (a, b) -> b)); // TODO: Remove merge function?
    }

    public List<Ticket> getTicketsOfType(int typeId) {
        return ticketRepository.getAllTickets()
                               .stream()
                               .filter(it -> it.getTypeId() == typeId)
                               .collect(Collectors.toList());
    }

    public List<Integer> getAvailableSeats(Ticket ticket, TicketType type) {
        if (!type.getAssignedSeating()) {
            return Collections.emptyList(); // hack
            //throw new IllegalArgumentException("Ticket type doesn't have assigned seating");
        }
        List<Integer> takenSeats = getTicketsOfType(type.getId())
                                                .stream()
                                                .filter(it -> it.getStatus() == Ticket.Status.PAID)
                                                .filter(it -> !it.getId().equals(ticket.getId()))
                                                .map(Ticket::getSeat)
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toList());
        return IntStream.range(0, type.getAmountAvailable())
                        .filter(it -> !takenSeats.contains(it))
                        .boxed()
                        .collect(Collectors.toList());
    }

    public void setSeat(Ticket ticket, int seat) {
        if (Integer.valueOf(seat).equals(ticket.getSeat())) {
            return;
        }
        TicketType type = getType(ticket.getTypeId());
        List<Integer> availableSeats = getAvailableSeats(ticket, type);
        if (!availableSeats.contains(seat)) {
            throw new IllegalArgumentException("Selected seat is not available");
        }
        ticketRepository.setSeat(ticket.getId(), seat);
    }
}
