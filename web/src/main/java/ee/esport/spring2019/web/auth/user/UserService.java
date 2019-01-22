package ee.esport.spring2019.web.auth.user;

import ee.esport.spring2019.web.auth.steam.SteamUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findBySteamId(String steamId) {
        Objects.requireNonNull(steamId);
        return userRepository.findBySteamId(steamId);
    }

    public User findByUserId(Integer userId) {
        Objects.requireNonNull(userId);
        return userRepository.findById(userId);
    }

    public User createUserForSteamUser(SteamUser steamUser) {
        return userRepository.create(User.builder()
                                         .name(steamUser.getName())
                                         .steamId(steamUser.getId())
                                         .role(UserRole.USER)
                                         .build());
    }

}
