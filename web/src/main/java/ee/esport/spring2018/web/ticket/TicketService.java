package ee.esport.spring2018.web.ticket;

import ee.esport.spring2018.web.auth.EsportClaims;
import ee.esport.spring2018.web.auth.SteamUser;
import ee.esport.spring2018.web.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class TicketService {

    @Resource
    private final TicketRepository ticketRepository;

    @Resource
    private final EmailService emailService;

    public List<TicketType> getAllTypes() {
        return ticketRepository.getAllTypes();
    }

    public TicketType getType(int typeId) {
        return ticketRepository.getTicketType(typeId);
    }

    public void addType(TicketType type) {
        ticketRepository.addType(type);
    }

    @SneakyThrows //temporarily wait for emailService to finish sending email, just in case
    public Ticket buyTicket(Ticket ticket, String referrer) {
        ticket.setDateCreated(OffsetDateTime.now());
        TicketType type = getType(ticket.getType().getId());
        if(!isActive(type)) {
            throw new IllegalArgumentException("Ticket type not currently available");
        }
        ticket.setStatus(type.hasRemaining() ? TicketStatus.AWAITING_PAYMENT : TicketStatus.IN_WAITING_LIST);
        ticket.setId(ticketRepository.addTicket(ticket));
        ticket.setType(type);
        String loginLink = createLoginLink(ticket, referrer);
        sendTicketCreationEmail(ticket, loginLink);
        return ticket;
    }

    private void sendTicketCreationEmail(Ticket ticket, String loginLink) throws InterruptedException,
                                                                                 ExecutionException {
        if(ticket.getStatus() == TicketStatus.AWAITING_PAYMENT) {
            emailService.sendTicketReservation(ticket, loginLink).get();
        } else {
            emailService.sendTicketWaiting(ticket, loginLink).get();
        }
    }

    public Ticket getTicket(int ticketId) {
        return ticketRepository.getTicket(ticketId);
    }

    public Integer getLoginLinkTicketId(String key) {
        return ticketRepository.getLoginLinkTicketId(key);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.getAllTickets();
    }

    public boolean isOwner(EsportClaims claims, Ticket ticket) {
        SteamUser steamUser = claims.getSteamUser();
        String claimsSteamId = steamUser != null ? steamUser.getId() : null;
        if(claimsSteamId != null && claimsSteamId.equals(ticket.getOwnerSteamId())) {
            return true;
        }
        Long claimsTicketId = claims.getTicketId();
        if(claimsTicketId != null && claimsTicketId.intValue() == ticket.getId()) {
            return true;
        }
        return false;
    }

    public boolean ownerCanCancel(Ticket ticket) {
        return Arrays.asList(TicketStatus.IN_WAITING_LIST, TicketStatus.AWAITING_PAYMENT)
                     .contains(ticket.getStatus());
    }

    public void cancelTicket(Ticket ticket, String referer) {
        if (ticket.getStatus() == TicketStatus.CANCELED) {
            throw new IllegalStateException("Cannot cancel ticket with status " + ticket.getStatus());
        }
        ticketRepository.setStatus(ticket.getId(), TicketStatus.CANCELED);
        emailService.sendTicketCanceled(ticket, createLoginLink(ticket, referer));
    }

    public void confirmTicketPaid(Ticket ticket, String referer) {
        if (ticket.getStatus() != TicketStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException("Cannot confirm ticket with status " + ticket.getStatus());
        }
        ticketRepository.setStatus(ticket.getId(), TicketStatus.PAID);
        emailService.sendTicketConfirmed(ticket, createLoginLink(ticket, referer));
    }

    private String createLoginLink(Ticket ticket, String referrer) {
        String loginLinkKey = ticketRepository.createLoginLink(ticket.getId());
        return UriComponentsBuilder.fromUriString(referrer)
                                   .replacePath("/")
                                   .fragment("/ticketLogin/" + loginLinkKey)
                                   .toUriString();
    }

    private boolean isActive(TicketType type) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime availableFrom = type.getAvailableFrom();
        OffsetDateTime availableUntil = type.getAvailableUntil();
        return (availableFrom == null || now.isAfter(availableFrom)) &&
               (availableUntil == null || now.isBefore(availableUntil));
    }

    public void addMember(int ticketId, TicketMember member) {
        Ticket ticket = ticketRepository.getTicket(ticketId);
        if (ticket.getMembers().size() >= ticket.getType().getTeamSize()) {
            throw new IllegalArgumentException("Cannot add a new member to ticket " + ticketId + ", " +
                                               "all slots already filled");
        }
        member.setId(null);
        ticketRepository.addMember(ticketId, member);
    }

    public void updateMember(int ticketId, TicketMember member) {
        if (member.getId() == null) {
            throw new IllegalArgumentException("Cannot update member, 'memberId' is null");
        }
        ticketRepository.updateMember(ticketId, member);
    }

    public void deleteMember(int ticketId, int memberId) {
        ticketRepository.deleteMember(ticketId, memberId);
    }

}
