package ee.esport.spring2018.web.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.impl.DefaultClaims;

import java.util.Map;
import java.util.UUID;

public class EsportClaims extends DefaultClaims {


    public static final String STEAM_USER = "steam_user";
    public static final String TICKET_ID = "ticket_id";
    public static final String ADMIN = "admin";
    public static final String CLAIMS_ID = "claims_id";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public EsportClaims() {
        initClaimsId();
    }

    public EsportClaims(Map<String, Object> map) {
        super(map);
        initClaimsId();
    }

    private void initClaimsId() {
        setValue(CLAIMS_ID, UUID.randomUUID().toString());
    }

    public String getClaimsId() {
        return get(CLAIMS_ID, String.class);
    }

    public EsportClaims setSteamUser(SteamUser steamUser) {
        setValue(STEAM_USER, steamUser);
        return this;
    }

    public EsportClaims setTicketId(long ticketId) {
        setValue(TICKET_ID, ticketId);
        return this;
    }

    public EsportClaims setAdmin(boolean isAdmin) {
        setValue(ADMIN, isAdmin);
        return this;
    }

    public SteamUser getSteamUser() {
        return objectMapper.convertValue(get(STEAM_USER, Object.class), SteamUser.class);
    }

    public Long getTicketId() {
        return get(TICKET_ID, Long.class);
    }

    public boolean isAdmin() {
        Boolean isAdmin = get(ADMIN, Boolean.class);
        return isAdmin != null ? isAdmin : false;
    }

}
