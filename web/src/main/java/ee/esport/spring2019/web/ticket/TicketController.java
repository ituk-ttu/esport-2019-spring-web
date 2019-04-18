package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.web.auth.user.User;
import ee.esport.spring2019.web.auth.user.UserRole;
import ee.esport.spring2019.web.auth.user.UserService;
import ee.esport.spring2019.web.email.EmailService;
import ee.esport.spring2019.web.ticket.domain.*;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/api")
public class TicketController {

    @Resource
    private TicketService ticketService;

    @Resource
    private UserService userService;

    @Resource
    private EmailService emailService;

    @GetMapping("/tickets/types")
    public ResponseEntity<List<TicketType>> getAllTicketTypes() {
        return new ResponseEntity<>(ticketService.getAllTypes(), HttpStatus.OK);
    }

    @GetMapping("/tickets/types/{typeId}")
    public ResponseEntity<TicketType> getTicketType(@PathVariable int typeId) {
        return new ResponseEntity<>(ticketService.getType(typeId), HttpStatus.OK);
    }

    @GetMapping("/tickets/offerings/visible")
    public ResponseEntity<List<TicketOffering>> getVisibleOfferings() {
        return new ResponseEntity<>(ticketService.getVisibleOfferings(), HttpStatus.OK);
    }

    @GetMapping("/tickets/offerings")
    public ResponseEntity<List<TicketOffering>> getAllOfferings() {
        return new ResponseEntity<>(ticketService.getAllOfferings(), HttpStatus.OK);
    }

    private void requireAdmin(User user) {
        requireLoggedIn(user);
        if (!user.getRole().isAtleast(UserRole.ADMIN)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/tickets/offerings/{id}")
    public ResponseEntity<TicketOffering> getOffering(@PathVariable int id, User user) {
        if (user != null && user.getRole().isAtleast(UserRole.ADMIN)) {
            return new ResponseEntity<>(ticketService.getFromAllOfferings(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(ticketService.getVisibleOffering(id), HttpStatus.OK);
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getAllTickets(User user) {
        requireAdmin(user);
        return new ResponseEntity<>(ticketService.getAllTickets(), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/tickets")
    public ResponseEntity<List<Ticket>> getUserTickets(@PathVariable int userId, User user) {
        requireLoggedIn(user);
        if (userId != user.getId() && !user.getRole().isAtleast(UserRole.ADMIN)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(ticketService.getUserTickets(userId), HttpStatus.OK);
    }


    @PostMapping("/tickets")
    public ResponseEntity<Ticket> buyTicket(@RequestBody TicketCreation ticketRequest, User user) {
        requireLoggedIn(user);
        if (!user.getRole().isAtleast(UserRole.ADMIN) && !ticketRequest.getOwnerId().equals(user.getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        Ticket boughtTicket = ticketService.createTicket(ticketRequest);
        return new ResponseEntity<>(boughtTicket, HttpStatus.OK);
    }

    @GetMapping("/tickets/ownerEmails")
    public ResponseEntity<Map<Integer, String>> getOwnerEmails(User user) {
        requireAdmin(user);
        return new ResponseEntity<>(ticketService.getOwnerEmails(), HttpStatus.OK);
    }

    @PostMapping("/tickets/{ticketId}/cancel")
    public ResponseEntity<Void> cancelTicket(@PathVariable int ticketId, User user) {
        requireLoggedIn(user);
        Ticket ticket = ticketService.getTicket(ticketId);
        if(!user.getRole().isAtleast(UserRole.ADMIN) &&
           !(ticket.getOwnerId().equals(user.getId()) && ticket.getStatus() != Ticket.Status.PAID)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        ticketService.cancelTicket(ticket);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/tickets/{ticketId}/sendEmail/{type}")
    public ResponseEntity<Void> sendUnsentEmail(@PathVariable int ticketId, @PathVariable String type, User user) {
        requireAdmin(user);
        Ticket ticket = ticketService.getTicket(ticketId);
        emailService.sendEmail(type, ticket, ticketService.getType(ticket.getTypeId()), ticketService.getVisibleOffering(ticket.getOfferingId()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/tickets/{ticketId}/confirm")
    public ResponseEntity<Void> confirmTicket(@PathVariable int ticketId, User user) {
        requireAdmin(user);
        Ticket ticket = ticketService.getTicket(ticketId);
        ticketService.confirmTicketPaid(ticket);
        return new ResponseEntity<>(HttpStatus.OK);
    }



    @PostMapping("/tickets/{ticketId}/members")
    public ResponseEntity<Ticket.Member> addMember(@PathVariable int ticketId, User user,
                                                   @RequestBody TicketMemberCandidate member) {
        membersDisabled();
        requireLoggedIn(user);
        Ticket ticket = ticketService.getTicket(ticketId);
        requireTicketAccess(user, ticket);
        return new ResponseEntity<>(ticketService.addMember(ticketId, member), HttpStatus.OK);
    }

    private void requireTicketAccess(User user, Ticket ticket) {
        if (!user.getRole().isAtleast(UserRole.ADMIN) && !ticket.getOwnerId().equals(user.getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
    }

    private void requireLoggedIn(User user) {
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/tickets/{ticketId}/members/{memberId}")
    public ResponseEntity<Ticket.Member> updateMember(@PathVariable int ticketId, @PathVariable int memberId, User user,
                                             @RequestBody TicketMemberCandidate member) {
        membersDisabled();
        requireLoggedIn(user);
        Ticket ticket = ticketService.getTicket(ticketId);
        requireTicketAccess(user, ticket);
        if (ticket.getMembers().stream().noneMatch(it -> Objects.equals(it.getId(), memberId))) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ticket does not have member with given ID");
        }
        return new ResponseEntity<>(ticketService.updateMember(ticketId, memberId, member), HttpStatus.OK);
    }

    @DeleteMapping("/tickets/{ticketId}/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable int ticketId, @PathVariable int memberId, User user) {
        membersDisabled();
        requireLoggedIn(user);
        Ticket ticket = ticketService.getTicket(ticketId);
        requireTicketAccess(user, ticket);
        if (ticket.getMembers().stream().noneMatch(it -> Objects.equals(it.getId(), memberId))) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ticket does not have member with given ID");
        }
        ticketService.deleteMember(ticketId, memberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void membersDisabled() {
        throw new IllegalStateException("Modifying members disabled");
    }

    @GetMapping("/tickets/{ticketId}/availableSeats")
    public ResponseEntity<List<Integer>> getAvailableSeats(@PathVariable int ticketId, User user) {
        requireLoggedIn(user);
        Ticket ticket = ticketService.getTicket(ticketId);
        requireTicketAccess(user, ticket);
        TicketType type = ticketService.getType(ticket.getTypeId());
        return new ResponseEntity<>(ticketService.getAvailableSeats(ticket, type), HttpStatus.OK);
    }


    @PostMapping("/tickets/{ticketId}/seat")
    public ResponseEntity<Void> selectSeat(@PathVariable int ticketId, User user,
                                           @RequestBody SelectSeatRequest request) {
        requireLoggedIn(user);
        Ticket ticket = ticketService.getTicket(ticketId);
        requireTicketAccess(user, ticket);
        ticketService.setSeat(ticket, request.getSeat());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Value
    private static class SelectSeatRequest {

        private final Integer seat;

    }

}
