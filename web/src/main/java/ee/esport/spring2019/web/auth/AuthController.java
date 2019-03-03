package ee.esport.spring2019.web.auth;

import ee.esport.spring2019.web.auth.jwt.JwtService;
import ee.esport.spring2019.web.auth.steam.SteamService;
import ee.esport.spring2019.web.auth.steam.SteamUser;
import ee.esport.spring2019.web.auth.user.User;
import ee.esport.spring2019.web.auth.user.UserCandidate;
import ee.esport.spring2019.web.auth.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final JwtService jwtService;
    private final SteamService steamService;
    private final UserService userService;

    @GetMapping("/steam/verify")
    public AuthResponse verifySteamLogin(HttpServletRequest request) {
        SteamUser steamUser = steamService.verifySteamLogin(request.getParameter("receivingUrl"),
                                                            request.getParameterMap());
        if (steamUser == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid steam login");
        }
        User user = userService.findBySteamId(steamUser.getId());
        if (user == null) {
            String registrationToken = jwtService.createSteamRegistrationToken(steamUser.getId());
            UserCandidate userCandidate = UserCandidate.builder()
                                                       .name(steamUser.getName())
                                                       .steamId(steamUser.getId())
                                                       .email("")
                                                       .build();
            return new NewUserResponse(userCandidate, registrationToken);
        }
        return new LoginSuccessResponse(user, jwtService.createForUser(user.getId()));
    }

    @PostMapping("/steam/register")
    public LoginSuccessResponse register(@RequestBody SteamRegistrationRequest request) {
        String steamId = jwtService.validateSteamRegistrationTokenAndGetSteamId(request.getRegistrationToken());
        Objects.requireNonNull(steamId);
        UserCandidate userCandidate = request.getUserDetails();
        if (!steamId.equals(userCandidate.getSteamId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Cannot create user with given Steam ID");
        }
        User user = userService.createUser(userCandidate);
        return new LoginSuccessResponse(user, jwtService.createForUser(user.getId()));
    }

    @Value
    public static class SteamRegistrationRequest {

        private final String registrationToken;
        private final UserCandidate userDetails;

    }

    public enum AuthResponseType {
        LOGIN, NEW_USER
    }

    public interface AuthResponse {

        AuthResponseType getType();

    }

    @Value
    public static class NewUserResponse implements AuthResponse {

        private final UserCandidate userDetails;
        private final String registrationToken;

        @Override
        public AuthResponseType getType() {
            return AuthResponseType.NEW_USER;
        }

    }

    @Value
    public static class LoginSuccessResponse implements AuthResponse {

        private final User user;
        private final String token;

        @Override
        public AuthResponseType getType() {
            return AuthResponseType.LOGIN;
        }

    }
}
