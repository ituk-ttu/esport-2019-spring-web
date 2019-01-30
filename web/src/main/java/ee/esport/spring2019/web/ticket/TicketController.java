package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.web.auth.user.User;
import ee.esport.spring2019.web.auth.user.UserRole;
import ee.esport.spring2019.web.auth.user.UserService;
import ee.esport.spring2019.web.core.WebClientUrl;
import ee.esport.spring2019.web.ticket.domain.Ticket;
import ee.esport.spring2019.web.ticket.domain.TicketCreation;
import ee.esport.spring2019.web.ticket.domain.TicketOffering;
import ee.esport.spring2019.web.ticket.domain.TicketType;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/api")
public class TicketController {

    @Resource
    private TicketService ticketService;

    @Resource
    private UserService userService;

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

    @GetMapping("/tickets/offerings/{id}")
    public ResponseEntity<TicketOffering> getOffering(@PathVariable int id) {
        //TODO: Admins should see all offerings, not only those which are visible
        return new ResponseEntity<>(ticketService.getVisibleOffering(id), HttpStatus.OK);
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getAllTickets(User user) {
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        if (!user.getRole().isAtleast(UserRole.ADMIN)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(ticketService.getAllTickets(), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/tickets")
    public ResponseEntity<List<Ticket>> getUserTickets(@PathVariable int userId, User user) {
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        if (userId != user.getId() && !user.getRole().isAtleast(UserRole.ADMIN)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(ticketService.getUserTickets(userId), HttpStatus.OK);
    }


    @PostMapping("/tickets")
    public ResponseEntity<Ticket> buyTicket(@RequestBody TicketCreation ticketRequest, User user) {
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        if (!user.getRole().isAtleast(UserRole.ADMIN) && !ticketRequest.getOwnerId().equals(user.getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        Ticket boughtTicket = ticketService.createTicket(ticketRequest);
        return new ResponseEntity<>(boughtTicket, HttpStatus.OK);
    }

    @PostMapping("/tickets/{ticketId}/cancel")
    public ResponseEntity<Void> cancelTicket(@PathVariable int ticketId, User user) {
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        Ticket ticket = ticketService.getTicket(ticketId);
        if(!user.getRole().isAtleast(UserRole.ADMIN) &&
           !(ticket.getOwnerId().equals(user.getId()) && ticket.getStatus() != Ticket.Status.PAID)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        ticketService.cancelTicket(ticket);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/tickets/{ticketId}/sendEmail")
    public ResponseEntity<Void> sendUnsentEmail(@PathVariable int ticketId, User user) {
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        if (!user.getRole().isAtleast(UserRole.ADMIN)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        ticketService.sendTicketCreationEmail(ticketService.getTicket(ticketId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/tickets/{ticketId}/confirm")
    public ResponseEntity<Void> confirmTicket(@PathVariable int ticketId, User user,
                                              @WebClientUrl String webClientUrl) {
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        if(!user.getRole().isAtleast(UserRole.ADMIN)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        Ticket ticket = ticketService.getTicket(ticketId);
        ticketService.confirmTicketPaid(ticket, webClientUrl);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/tickets/{ticketId}/members")
    public ResponseEntity<Ticket.Member> addMember(@PathVariable int ticketId, User user) {
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        Ticket ticket = ticketService.getTicket(ticketId);
        if (!user.getRole().isAtleast(UserRole.ADMIN) && ticket.getOwnerId().equals(user.getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        throw new NotImplementedException("Adding member to ticket not yet implemented");
    }

    @DeleteMapping("/tickets/{ticketId}/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable int ticketId, @PathVariable int memberId, User user) {
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        Ticket ticket = ticketService.getTicket(ticketId);
        if (!user.getRole().isAtleast(UserRole.ADMIN) && ticket.getOwnerId().equals(user.getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        if (ticket.getMembers().stream().noneMatch(member -> member.getId() == memberId)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ticket does not have member with given ID");
        }
        ticketService.deleteMember(ticketId, memberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
