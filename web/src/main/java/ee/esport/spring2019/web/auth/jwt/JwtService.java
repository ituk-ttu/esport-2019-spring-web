package ee.esport.spring2019.web.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import io.jsonwebtoken.impl.DefaultJwtParser;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@ConfigurationProperties("jwt.signature")
public class JwtService {

    private static final String ISSUER = "TalTech e-Sport";
    private static final Pattern JWT_PATTERN = Pattern.compile("^Bearer (.+)$");
    private static final Duration JWT_LIFESPAN = Duration.of(7, ChronoUnit.DAYS);

    @Setter private SignatureAlgorithm algorithm;
    @Setter private String key;

    public String createForUser(Integer userId) {
        Objects.requireNonNull(userId);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Claims claims = new EsportClaims().setUserId(userId)
                                          .setExpiration(asLegacyDate(now.plus(JWT_LIFESPAN)))
                                          .setIssuedAt(asLegacyDate(now))
                                          .setIssuer(ISSUER);
        return new DefaultJwtBuilder().setClaims(claims)
                                      .signWith(algorithm, key)
                                      .compact();
    }

    public Integer validateAndGetUserId(HttpServletRequest request) {
        String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(tokenHeader == null) {
            return null;
        }
        Matcher matcher = JWT_PATTERN.matcher(tokenHeader);
        if(!matcher.find()) {
            return null;
        }
        String token = matcher.group(1);
        return validateAndGetUserId(token);
    }

    public Integer validateAndGetUserId(String token) {
        Jws<Claims> parsedToked = new DefaultJwtParser().setSigningKey(key)
                                                        .parseClaimsJws(token);
        EsportClaims claims = new EsportClaims(parsedToked.getBody());
        if(Instant.now().isAfter(claims.getExpiration().toInstant())) {
            return null;
        }
        return claims.getUserId();
    }

    private Date asLegacyDate(LocalDateTime exp) {
        return Date.from(exp.toInstant(ZoneOffset.UTC));
    }

}
