package no.nav.foreldrepenger.lookup;

import static no.nav.foreldrepenger.StreamUtil.not;
import static no.nav.foreldrepenger.lookup.Constants.ISSUER;
import static no.nav.security.oidc.OIDCConstants.OIDC_VALIDATION_CONTEXT;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;

import no.nav.foreldrepenger.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.context.TokenContext;

@Component
public class TokenHandler {

    private final OIDCRequestContextHolder ctxHolder;

    public TokenHandler(OIDCRequestContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public boolean erAutentisert() {
        return getSubject() != null;
    }

    public Date getExp() {
        return Optional.ofNullable(claimSet())
                .map(JWTClaimsSet::getExpirationTime)
                .orElse(null);
    }

    public Fødselsnummer getSubject() {
        return Optional.ofNullable(claimSet())
                .map(JWTClaimsSet::getSubject)
                .map(Fødselsnummer::new)
                .orElse(null);
    }

    public Fødselsnummer autentisertBruker() {
        return Optional.ofNullable(getSubject())
                .orElseThrow(forbidden("Fant ikke subject"));
    }

    private static Supplier<? extends ForbiddenException> forbidden(String msg) {
        return () -> new ForbiddenException(msg);
    }

    private JWTClaimsSet claimSet() {
        return Optional.ofNullable(claims())
                .map(OIDCClaims::getClaimSet)
                .orElse(null);
    }

    private OIDCClaims claims() {
        return Optional.ofNullable(context())
                .map(s -> s.getClaims(ISSUER))
                .orElse(null);
    }

    private OIDCValidationContext context() {
        return Optional.ofNullable(ctxHolder.getRequestAttribute(OIDC_VALIDATION_CONTEXT))
                .map(s -> OIDCValidationContext.class.cast(s))
                .orElse(null);
    }

    public String getToken() {
        return Optional.ofNullable(context())
                .map(c -> c.getToken(ISSUER))
                .filter(not(Objects::isNull))
                .map(TokenContext::getIdToken)
                .orElseThrow(forbidden("Fant ikke token"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ctxHolder=" + ctxHolder + "]";
    }

}
