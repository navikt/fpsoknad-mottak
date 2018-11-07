package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.http.Constants.ISSUER;
import static no.nav.security.oidc.OIDCConstants.OIDC_VALIDATION_CONTEXT;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.http.errorhandling.UnauthenticatedException;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;

@Component
public class TokenHandler {

    private final OIDCRequestContextHolder ctxHolder;

    public TokenHandler(OIDCRequestContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public boolean erAutentisert() {
        try {
            autentisertBruker();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getToken() {
        return Optional.ofNullable(context().getToken(ISSUER))
                .map(s -> s.getIdToken())
                .orElseThrow(() -> new UnauthenticatedException("Fant ikke token for issuer " + ISSUER));
    }

    public String getFnr() {
        return autentisertBruker().getFnr();
    }

    public Fødselsnummer autentisertBruker() {
        OIDCValidationContext context = Optional.ofNullable(context())
                .orElseThrow(() -> new UnauthenticatedException("Fant ikke context"));

        OIDCClaims claims = Optional.ofNullable(context.getClaims(ISSUER))
                .orElseThrow(() -> new UnauthenticatedException("Fant ikke claims for issuer " + ISSUER));

        JWTClaimsSet claimSet = Optional.ofNullable(claims.getClaimSet())
                .orElseThrow(() -> new UnauthenticatedException("Fant ikke claim set"));

        return Optional.ofNullable(claimSet.getSubject())
                .map(String::trim)
                .map(Fødselsnummer::new)
                .orElseThrow(() -> new UnauthenticatedException("Fant ikke subject"));

    }

    private OIDCValidationContext context() {
        return Optional.ofNullable(ctxHolder.getRequestAttribute(OIDC_VALIDATION_CONTEXT))
                .map(s -> OIDCValidationContext.class.cast(s))
                .orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ctxHolder=" + ctxHolder + "]";
    }
}