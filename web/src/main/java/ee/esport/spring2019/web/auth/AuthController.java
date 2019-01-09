package ee.esport.spring2019.web.auth;

import ee.esport.spring2019.web.ticket.TicketService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api")
public class AuthController {

    @Resource
    private TicketService ticketService;

    @Resource
    private JwtService jwtService;

    @Resource
    private SteamService steamService;

    @GetMapping("/ticket/token/{loginLinkKey}")
    public ResponseEntity<Void> getTicketJwt(@PathVariable String loginLinkKey) {
        Integer ticketId = ticketService.getLoginLinkTicketId(loginLinkKey);
        if (ticketId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        EsportClaims claims = new EsportClaims().setTicketId(ticketId);
        return new ResponseEntity<>(getAuthorizationHeaders(claims), HttpStatus.OK);
    }

    @GetMapping("/steam/loginLink")
    public ResponseEntity<String> getSteamLoginLink(@RequestParam String returnTo) {
        return new ResponseEntity<>(steamService.getLoginUrl(returnTo), HttpStatus.OK);
    }

    @GetMapping("/steam/verify")
    public ResponseEntity<Void> verifySteamLogin(HttpServletRequest request) {
        SteamUser user = steamService.verifySteamLogin(request.getParameter("receivingUrl"),
                                                       request.getParameterMap());
        EsportClaims claims = new EsportClaims().setSteamUser(user);
        return new ResponseEntity<>(getAuthorizationHeaders(claims), HttpStatus.OK);
    }

    private HttpHeaders getAuthorizationHeaders(EsportClaims claims) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createFromClaims(claims));
        return headers;
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<Void> refreshToken(EsportClaimsHolder claimsHolder) {
        EsportClaims claims = claimsHolder.get();
        if(claims == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(getAuthorizationHeaders(claims), HttpStatus.OK);
    }
}
