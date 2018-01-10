package ee.esport.spring2018.web.ticket;

import ee.esport.spring2018.web.auth.EsportClaims;
import ee.esport.spring2018.web.auth.EsportClaimsHolder;
import ee.esport.spring2018.web.auth.SteamUser;
import ee.esport.spring2018.web.web.WebClientUrl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api")
public class TicketController {

    @Resource
    private TicketService ticketService;

    @GetMapping("/ticketTypes")
    public ResponseEntity<List<TicketType>> getAllTicketTypes() {
        return new ResponseEntity<>(ticketService.getAllTypes(), HttpStatus.OK);
    }

    @PostMapping("/ticketType")
    public ResponseEntity<Void> addTicketTypes(@RequestBody TicketType type, EsportClaimsHolder claimsHolder) {
        if (!claimsHolder.get().isAdmin()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ticketService.addType(type);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/ticketType/{typeId}")
    public ResponseEntity<TicketType> getTicketType(@PathVariable int typeId) {
        return new ResponseEntity<>(ticketService.getType(typeId), HttpStatus.OK);
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getAllTickets(EsportClaimsHolder claimsHolder) {
        List<Ticket> allTickets = ticketService.getAllTickets();
        if(claimsHolder.get().isAdmin()) {
            return new ResponseEntity<>(allTickets, HttpStatus.OK);
        }
        List<Ticket> accessibleTickets = new ArrayList<>();
        SteamUser steamUser = claimsHolder.get().getSteamUser();
        if(steamUser != null) {
            allTickets.stream()
                      .filter(ticket -> steamUser.getId().equals(ticket.getOwnerSteamId()))
                      .forEach(accessibleTickets::add);
        }
        Long ticketId = claimsHolder.get().getTicketId();
        if(ticketId != null) {
            allTickets.stream()
                      .filter(ticket -> ticketId.intValue() == ticket.getId())
                      .forEach(accessibleTickets::add);
        }
        return new ResponseEntity<>(accessibleTickets, HttpStatus.OK);
    }

    @PostMapping("/ticket")
    public ResponseEntity<Ticket> buyTicket(@RequestBody Ticket ticket, EsportClaimsHolder claimsHolder,
                                            @WebClientUrl String webClientUrl) {
        SteamUser steamUser = claimsHolder.get().getSteamUser();
        ticket.setOwnerSteamId(steamUser != null ? steamUser.getId() : null);
        Ticket boughtTicket = ticketService.buyTicket(ticket, webClientUrl);
        return new ResponseEntity<>(boughtTicket, HttpStatus.OK);
    }

    @PostMapping("/ticket/{ticketId}/cancel")
    public ResponseEntity<Void> cancelTicket(@PathVariable int ticketId, EsportClaimsHolder claimsHolder,
                                             @WebClientUrl String webClientUrl) {
        Ticket ticket = ticketService.getTicket(ticketId);
        EsportClaims claims = claimsHolder.get();
        if(ticket.getStatus() == TicketStatus.CANCELED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(!canCancelTicket(ticket, claims)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ticketService.cancelTicket(ticket, webClientUrl);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean canCancelTicket(Ticket ticket, EsportClaims claims) {
        return claims.isAdmin() || ticketService.isOwner(claims, ticket) && ticketService.ownerCanCancel(ticket);
    }

    @PostMapping("/ticket/{ticketId}/confirm")
    public ResponseEntity<Void> confirmTicket(@PathVariable int ticketId, EsportClaimsHolder claimsHolder,
                                              @WebClientUrl String webClientUrl) {
        Ticket ticket = ticketService.getTicket(ticketId);
        EsportClaims claims = claimsHolder.get();
        if(!claims.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ticketService.confirmTicketPaid(ticket, webClientUrl);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
