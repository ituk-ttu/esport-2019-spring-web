package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.web.auth.user.User;
import ee.esport.spring2019.web.auth.user.UserRole;
import ee.esport.spring2019.web.ticket.domain.Ticket;
import ee.esport.spring2019.web.ticket.domain.TicketCert;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/api")
public class TicketCertController {

    @Resource
    private TicketService ticketService;

    @Resource
    private TicketCertService certService;

    private final MultiValueMap<String, SseEmitter> clientSessions = new LinkedMultiValueMap<>();
    private final MultiValueMap<String, SseEmitter> cashierSessions = new LinkedMultiValueMap<>();

    @PostMapping("/tickets/{ticketId}/certs/generate")
    public ResponseEntity<GenerateCertsResponse> generateCerts(@PathVariable int ticketId, User user) {
        requireAdmin(user);
        return new ResponseEntity<>(new GenerateCertsResponse(ticketService.getTicket(ticketId),
                                                              certService.generateCerts(ticketId)),
                                    HttpStatus.OK);
    }

    @PostMapping("/tickets/all/certs/generate")
    public ResponseEntity<Void> generateCerts(User user) {
        requireAdmin(user);
        certService.genAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/certs/{certCode}/clientEvents")
    public ResponseEntity<SseEmitter> certClientEvents(@PathVariable String certCode) {
        return new ResponseEntity<>(getSseEmitter(certCode, clientSessions), HttpStatus.OK);
    }

    @RequestMapping("/certs/{certCode}/cashierEvents")
    public ResponseEntity<SseEmitter> certCashierEvents(@PathVariable String certCode) {
        return new ResponseEntity<>(getSseEmitter(certCode, cashierSessions), HttpStatus.OK);
    }

    private SseEmitter getSseEmitter(String certCode, MultiValueMap<String, SseEmitter> sessionStore) {
        SseEmitter emitter = new SseEmitter(Duration.ofSeconds(60).toMillis());
        sessionStore.add(certCode, emitter);
        log.info("Start session " + certCode);
        emitter.onCompletion(() -> {
            log.info("End session " + certCode);
            List<SseEmitter> emitters = sessionStore.get(certCode);
            if (emitters == null) {
                return;
            }
            emitters.remove(emitter);
        });
        return emitter;
    }

    @PostMapping("/certs/{certCode}/askAcceptance")
    public ResponseEntity<MessageResult> askAcceptance(User user, @PathVariable String certCode) {
        requireAdmin(user);
        Ticket certTicket = certService.getCertTicket(certCode);
        if (certTicket == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            sendEvent(certCode, Event.ASK_ACCEPTANCE, clientSessions);
        } catch (Exception e) {
            return new ResponseEntity<>(MessageResult.NO_SESSION, HttpStatus.OK);
        }
        return new ResponseEntity<>(MessageResult.SUCCESS, HttpStatus.OK);
    }

    private void sendEvent(@PathVariable String certCode, Event event, MultiValueMap<String, SseEmitter> sessionStore) {
        List<SseEmitter> sessions = getSessions(certCode, sessionStore);
        if (sessions.isEmpty()) {
            log.warn("It's empty");
            throw new IllegalStateException();
        }
        boolean works = false;
        for (SseEmitter session : sessions) {
            try {
                session.send(SseEmitter.event()
                                       .data(event.toString())
                                       .id(UUID.randomUUID().toString())
                                       .name("EVENT"));
                works = true;
            } catch (Exception e) {
                log.warn("Something is f*cked", e);
            }
        }
        if(!works) {
            throw new IllegalStateException();
        }
    }

    @PostMapping("/certs/{certCode}/giveAcceptance")
    public ResponseEntity<MessageResult> giveAcceptance(User user, @PathVariable String certCode) {
        try {
            sendEvent(certCode, Event.GIVE_ACCEPTANCE, cashierSessions);
        } catch (Exception e) {
            return new ResponseEntity<>(MessageResult.NO_SESSION, HttpStatus.OK);
        }
        return new ResponseEntity<>(MessageResult.SUCCESS, HttpStatus.OK);
    }

    @PostMapping("/certs/{certCode}/use")
    public ResponseEntity<MessageResult> use(User user, @PathVariable String certCode) {
        requireAdmin(user);
        certService.useCert(certCode);
        try {
            sendEvent(certCode, Event.FINISHED, clientSessions);
        } catch (Exception e) {
            return new ResponseEntity<>(MessageResult.NO_SESSION, HttpStatus.OK);
        }
        return new ResponseEntity<>(MessageResult.SUCCESS, HttpStatus.OK);
    }

    public List<SseEmitter> getSessions(String certCode, MultiValueMap<String, SseEmitter> sessionStore) {
        List<SseEmitter> sseEmitters = sessionStore.get(certCode);
        return sseEmitters != null ? sseEmitters : Collections.emptyList();
    }

    @GetMapping("/certs/{certCode}/ticket")
    public ResponseEntity<Ticket> certTicket(User user, @PathVariable String certCode) {
        requireAdmin(user);
        Ticket certTicket = certService.getCertTicket(certCode);
        if (certTicket == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(certTicket, HttpStatus.OK);
    }

    @GetMapping("/ticket/{ticketId}/certs")
    public ResponseEntity<List<TicketCert>> getCerts(User user, @PathVariable int ticketId) {
        requireAdmin(user);
        return new ResponseEntity<>(certService.getTicketCerts(ticketId), HttpStatus.OK);
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

    private enum MessageResult {
        SUCCESS, NO_SESSION
    }

    private enum Event {
        ASK_ACCEPTANCE, GIVE_ACCEPTANCE, FINISHED
    }

    @Value
    private static class EventWrapper {

        private final Event event;

    }

}
