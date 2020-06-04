package no.nav.foreldrepenger.mottak.util;

import static java.time.Instant.now;
import static no.nav.foreldrepenger.mottak.Constants.ISSUER;

import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.util.DateUtils;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.core.jwt.JwtTokenClaims;

@Component
public class TokenUtil {
    private final TokenValidationContextHolder ctxHolder;

    public TokenUtil(TokenValidationContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public boolean erUtløpt() {
        return Optional.ofNullable(getExpiryDate())
                .filter(d -> d.before(Date.from(now())))
                .isPresent();
    }

    public String bearerToken() {
        return "BEARER " + getToken();
    }

    public String getToken() {
        return ctxHolder.getTokenValidationContext().getJwtToken(ISSUER).getTokenAsString();
    }

    public boolean erAutentisert() {
        return getSubject() != null;
    }

    public Date getExpiryDate() {
        return Optional.ofNullable(claimSet())
                .map(c -> c.get("exp"))
                .map(this::getDateClaim)
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

    public Fødselsnummer autentisertFNR() {
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ctxHolder=" + ctxHolder + "]";
    }
}