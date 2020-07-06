package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.Constants.ISSUER;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.util.DateUtils;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.core.jwt.JwtTokenClaims;

@Component
public class TokenUtil {

    public static final String BEARER = "Bearer ";

    private final TokenValidationContextHolder ctxHolder;

    public TokenUtil(TokenValidationContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public boolean erUtløpt() {
        return Optional.ofNullable(getExpiration())
                .filter(d -> d.isBefore(LocalDateTime.now()))
                .isPresent();
    }

    public String bearerToken() {
        if (erAutentisert()) {
            return BEARER + getToken();
        }
        return null;
    }

    public String getToken() {
        return getJWTToken().getTokenAsString();
    }

    public boolean erAutentisert() {
        return getSubject() != null;
    }

    public LocalDateTime getExpiration() {
        return Optional.ofNullable(claimSet())
                .map(c -> c.get("exp"))
                .map(this::getDateClaim)
                .map(TimeUtil::fraDato)
                .orElse(null);
    }

    public String getSubject() {
        return Optional.ofNullable(claimSet())
                .map(JwtTokenClaims::getSubject)
                .orElse(null);
    }

    public String autentisertBruker() {
        return Optional.ofNullable(getSubject())
                .orElseThrow(unauthenticated("Fant ikke subject"));
    }

    public Fødselsnummer fnr() {
        return new Fødselsnummer(autentisertBruker());
    }

    private static Supplier<? extends JwtTokenValidatorException> unauthenticated(String msg) {
        return () -> new JwtTokenValidatorException(msg);
    }

    private JwtTokenClaims claimSet() {
        return Optional.ofNullable(context())
                .map(s -> s.getClaims(ISSUER))
                .orElse(null);
    }

    private TokenValidationContext context() {
        return Optional.ofNullable(ctxHolder.getTokenValidationContext())
                .orElse(null);
    }

    private Date getDateClaim(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof Number) {
            return DateUtils.fromSecondsSinceEpoch(((Number) value).longValue());
        }
        return null;

    }

    public JwtToken getJWTToken() {
        return ctxHolder.getTokenValidationContext().getJwtToken(ISSUER);

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ctxHolder=" + ctxHolder + "]";
    }

}