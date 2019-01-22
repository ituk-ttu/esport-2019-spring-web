package ee.esport.spring2019.web.auth.user;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static ee.esport.spring2018.jooq.Tables.USERS;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DSLContext dsl;

    public User findBySteamId(String steamId) {
        return dsl.selectFrom(USERS)
                  .where(USERS.STEAM_ID.eq(steamId))
                  .fetchAnyInto(User.class);
    }

    public User findById(Integer id) {
        return dsl.selectFrom(USERS)
                  .where(USERS.ID.eq(id))
                  .fetchAnyInto(User.class);
    }

    public User create(User user) {
        return dsl.insertInto(USERS)
                  .set(USERS.NAME, user.getName())
                  .set(USERS.STEAM_ID, user.getSteamId())
                  .set(USERS.ROLE, user.getRole().name())
                  .returning()
                  .fetchOne()
                  .into(User.class);
    }

}
