package ee.esport.spring2019.web.auth.jwt;

import io.jsonwebtoken.impl.DefaultClaims;

import java.util.Map;

public class SteamRegistrationClaims extends DefaultClaims {

    private static final String STEAM_ID = "steamIdId";

    public SteamRegistrationClaims() {}

    public SteamRegistrationClaims(Map<String, Object> map) {
        super(map);
    }

    public SteamRegistrationClaims setSteamId(String steamId) {
        setValue(STEAM_ID, steamId);
        return this;
    }

    public String getSteamId() {
        return get(STEAM_ID, String.class);
    }

}
