package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.web.auth.user.User;
import ee.esport.spring2019.web.auth.user.UserRole;
import ee.esport.spring2019.web.ticket.domain.Ticket;
import ee.esport.spring2019.web.ticket.domain.TicketCert;
import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/api")
public class TicketCertController {

    @Resource
    private TicketService ticketService;

    @Resource
    private TicketCertService certService;

    @PostMapping("/tickets/{ticketId}/certs/generate")
    public ResponseEntity<GenerateCertsResponse> generateCerts(@PathVariable int ticketId, User user) {
        requireAdmin(user);
        return new ResponseEntity<>(new GenerateCertsResponse(ticketService.getTicket(ticketId),
                                                              certService.generateCerts(ticketId)),
                                    HttpStatus.OK);
    }

    private void requireAdmin(User user) {
        requireLoggedIn(user);
        if (!user.getRole().isAtleast(UserRole.ADMIN)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
    }

    private void requireLoggedIn(User user) {
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    @Value
    private static class GenerateCertsResponse {

        @NonNull private Ticket ticket;
        @NonNull private List<TicketCert> certs;

    }

}
