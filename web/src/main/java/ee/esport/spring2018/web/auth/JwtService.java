package ee.esport.spring2018.web.auth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import io.jsonwebtoken.impl.DefaultJwtParser;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@ConfigurationProperties("jwt.signature")
@RequiredArgsConstructor
@AllArgsConstructor
public class JwtService {

    private static final String ISSUER = "TTÜ e-Sport";
    private static final Pattern JWT_PATTERN = Pattern.compile("^Bearer (.+)$");
    private static final Duration LIFESPAN = Duration.of(7, ChronoUnit.DAYS);

    @Setter private SignatureAlgorithm algorithm;
    @Setter private String key;

    @Resource
    private AuthRepository authRepository;

    public String createFromClaims(EsportClaims claims) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        if (claims.getSteamUser() != null && authRepository.isAdmin(claims.getSteamUser().getId())) {
            claims.setAdmin(true);
        }
        return new DefaultJwtBuilder().setClaims(claims)
                                      .setExpiration(asLegacyDate(now.plus(LIFESPAN)))
                                      .setIssuedAt(asLegacyDate(now))
                                      .setIssuer(ISSUER)
                                      .signWith(algorithm, key)
                                      .compact();
    }

    public EsportClaims validateAndGetClaims(HttpServletRequest request) {
        String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(tokenHeader == null) {
            return null;
        }
        Matcher matcher = JWT_PATTERN.matcher(tokenHeader);
        if(!matcher.find()) {
            return null;
        }
        String token = matcher.group(1);
        return validateAndGetClaims(token);
    }

    public EsportClaims validateAndGetClaims(String token) {
        Jws<Claims> parsedToked = new EsportJwtParser().setSigningKey(key).parseClaimsJws(token);
        EsportClaims claims = new EsportClaims(parsedToked.getBody());
        if(Instant.now().isAfter(claims.getExpiration().toInstant())) {
            return null;
        }
        return claims;
    }

    @SneakyThrows
    public String hashToken(String token) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
        return String.valueOf(Hex.encodeHex(hash));
    }

    public boolean isHashValid(String token, String hash) {
        return hashToken(token).equals(hash);
    }

    private Date asLegacyDate(LocalDateTime exp) {
        return Date.from(exp.toInstant(ZoneOffset.UTC));
    }

    private class EsportJwtParser extends DefaultJwtParser {

        private final ObjectReader objectReader;

        public EsportJwtParser() {
            objectReader = new ObjectMapper().readerFor(Map.class)
                                             .withFeatures(DeserializationFeature.USE_LONG_FOR_INTS);
        }

        @SuppressWarnings("unchecked")
        protected Map<String, Object> readValue(String val) {
            try {
                return objectReader.readValue(val);
            } catch (IOException e) {
                throw new MalformedJwtException("Unable to read JSON value: " + val, e);
            }
        }

    }

}
