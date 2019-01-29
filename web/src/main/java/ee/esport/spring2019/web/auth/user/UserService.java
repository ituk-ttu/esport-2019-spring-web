package ee.esport.spring2019.web.auth.user;

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

    public String getUserEmail(Integer userId) {
        Objects.requireNonNull(userId);
        return userRepository.getUserEmail(userId);
    }

    public User createUser(UserCandidate userCandidate) {
        return userRepository.create(User.builder()
                                         .name(userCandidate.getName())
                                         .steamId(userCandidate.getSteamId())
                                         .email(userCandidate.getEmail())
                                         .role(UserRole.USER)
                                         .build());
    }

}
