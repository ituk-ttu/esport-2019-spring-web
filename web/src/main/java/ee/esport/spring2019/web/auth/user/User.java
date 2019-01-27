package ee.esport.spring2019.web.auth.user;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class User {

    private final Integer id;
    @NonNull private final String name;
    @NonNull private final String steamId;
    @NonNull private final UserRole role;
    private final String email;

}
