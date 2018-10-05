package no.nav.foreldrepenger.mottak.util;

import static no.nav.security.oidc.OIDCConstants.OIDC_VALIDATION_CONTEXT;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.http.errorhandling.ForbiddenException;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;

@Component
public class FnrExtractor {

    private final OIDCRequestContextHolder ctxHolder;

    public FnrExtractor(OIDCRequestContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public boolean hasToken() {
        try {
            fnrFromToken();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getToken() {
        return Optional.ofNullable(context().getToken("selvbetjening"))
                .map(s -> s.getIdToken())
                .orElseThrow(() -> new ForbiddenException("Fant ikke token"));
    }

    public Fødselsnummer fnrFromToken() {
        OIDCValidationContext context = context();
        if (context == null) {
            throw new ForbiddenException("Fant ikke context");
        }
        OIDCClaims claims = context.getClaims("selvbetjening");
        if (claims == null) {
            throw new ForbiddenException("Fant ikke claims");
        }
        JWTClaimsSet claimSet = claims.getClaimSet();
        if (claimSet == null) {
            throw new ForbiddenException("Fant ikke claim set");
        }
        String fnr = claimSet.getSubject();
        if (fnr == null || fnr.trim().isEmpty()) {
            throw new ForbiddenException("Fant ikke subject");
        }
        return new Fødselsnummer(fnr);
    }

    private OIDCValidationContext context() {
        return Optional.ofNullable(ctxHolder.getRequestAttribute(OIDC_VALIDATION_CONTEXT))
                .map(s -> OIDCValidationContext.class.cast(s))
                .orElseThrow(() -> new ForbiddenException("Fant ikke context"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ctxHolder=" + ctxHolder + "]";
    }
}