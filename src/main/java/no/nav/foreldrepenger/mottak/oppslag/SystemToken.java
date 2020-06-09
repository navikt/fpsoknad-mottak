package no.nav.foreldrepenger.mottak.oppslag;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.util.StringUtil;
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

    public boolean isExpired(long slack) {
        return LocalDateTime.now().isAfter(LocalDateTime.now().plusSeconds(expiresIn).minusSeconds(slack));
    }

    private JwtToken getAccessToken() {
        return accessToken;
    }

    public String getToken() {
        return getAccessToken().getTokenAsString();
    }

    public Date getExpiration() {
        return getAccessToken().getJwtTokenClaims().getExpirationTime();
    }

    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[accessToken=" + StringUtil.limit(accessToken.getTokenAsString(), 12)
                + ", expiresIn="
                + expiresIn
                + ", tokenType=" + tokenType + "]";
    }

}