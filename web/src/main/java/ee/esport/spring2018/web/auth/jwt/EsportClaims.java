package ee.esport.spring2018.web.auth.jwt;

import io.jsonwebtoken.impl.DefaultClaims;

import java.util.Map;

public class EsportClaims extends DefaultClaims {

    private static final String USER_ID = "userId";

    public EsportClaims() {}

    public EsportClaims(Map<String, Object> map) {
        super(map);
    }

    public EsportClaims setUserId(Integer userId) {
        setValue(USER_ID, userId);
        return this;
    }

    public Integer getUserId() {
        return get(USER_ID, Integer.class);
    }

}
