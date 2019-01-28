package ee.esport.spring2019.web.auth.user;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class UserCandidate {

    @NonNull private final String name;
    @NonNull private final String steamId;
    @NonNull private final String email;

}
