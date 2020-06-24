package no.nav.foreldrepenger.mottak.oppslag.sts;

import static no.nav.foreldrepenger.mottak.util.StringUtil.limit;
import static no.nav.foreldrepenger.mottak.util.TimeUtil.localDateTime;

import java.time.Duration;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.security.token.support.core.jwt.JwtToken;

public class SystemToken {

    private final JwtToken accessToken;

    private final Long expiresIn;

    private final String tokenType;

    @JsonCreator
    public SystemToken(@JsonProperty("access_token") JwtToken accessToken, @JsonProperty("expires_in") Long expiresIn,
            @JsonProperty("token_type") String tokenType) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
    }

    @Deprecated
    public boolean isExpired(long slack) {
        return LocalDateTime.now().isAfter(getExpiration().minusSeconds(slack));
    }

    public boolean isExpired(Duration slack) {
        return LocalDateTime.now().isAfter(getExpiration().minus(slack));
    }

    private JwtToken getAccessToken() {
        return accessToken;
    }

    public String getToken() {
        return getAccessToken().getTokenAsString();
    }

    public LocalDateTime getExpiration() {
        return localDateTime(getAccessToken().getJwtTokenClaims().getExpirationTime());
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[accessToken=" + limit(accessToken.getTokenAsString(), 12)
                + ", expires=" + getExpiration()
                + ", tokenType=" + tokenType + "]";
    }

}