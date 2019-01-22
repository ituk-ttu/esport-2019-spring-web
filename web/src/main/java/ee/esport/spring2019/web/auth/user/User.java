package ee.esport.spring2019.web.auth.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class User {

    private final Integer id;
    private final String name;
    private final String steamId;
    private final UserRole role;

}
