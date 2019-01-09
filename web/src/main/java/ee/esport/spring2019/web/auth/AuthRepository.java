package ee.esport.spring2019.web.auth;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import static ee.esport.spring2019.jooq.Tables.ADMINS;

@Repository
public class AuthRepository {

    @Resource
    private DSLContext dsl;

    public boolean isAdmin(String steamId) {
        return dsl.selectCount()
                  .from(ADMINS)
                  .where(ADMINS.STEAM_ID.eq(steamId))
                  .fetchOne(0, int.class) > 0;
    }

}
