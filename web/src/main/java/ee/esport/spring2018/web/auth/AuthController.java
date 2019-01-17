package ee.esport.spring2018.web.auth;

import ee.esport.spring2018.web.auth.jwt.JwtService;
import ee.esport.spring2018.web.auth.steam.SteamService;
import ee.esport.spring2018.web.auth.steam.SteamUser;
import ee.esport.spring2018.web.auth.user.User;
import ee.esport.spring2018.web.auth.user.UserService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final JwtService jwtService;
    private final SteamService steamService;
    private final UserService userService;

    @GetMapping("/steam/loginLink")
    public ResponseEntity<String> getSteamLoginLink(@RequestParam String returnTo) {
        return new ResponseEntity<>(steamService.getLoginUrl(returnTo), HttpStatus.OK);
    }

    @GetMapping("/steam/verify")
    public AuthResponse verifySteamLogin(HttpServletRequest request) {
        SteamUser steamUser = steamService.verifySteamLogin(request.getParameter("receivingUrl"),
                                                            request.getParameterMap());
        User user = userService.findBySteamId(steamUser.getId());
        if (user == null) {
            user = userService.createUserForSteamUser(steamUser);
        }
        return new AuthResponse(user, jwtService.createForUser(user.getId()));
    }

    @Value
    public static class AuthResponse {

        private final User user;
        private final String token;

    }
}
