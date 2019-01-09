package ee.esport.spring2019.web.auth;

import lombok.RequiredArgsConstructor;

// Can't inject EsportsClaim directly because of MapMethodProcessor
@RequiredArgsConstructor
public class EsportClaimsHolder {

    private final EsportClaims claims;

    public EsportClaims get() {
        return claims != null ? claims : new EsportClaims();
    }
}
