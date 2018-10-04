package no.nav.foreldrepenger.mottak.util;

import static no.nav.security.oidc.OIDCConstants.OIDC_VALIDATION_CONTEXT;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;

import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;

@Component
public final class FnrExtractor {

    private final OIDCRequestContextHolder ctxHolder;

    public FnrExtractor(OIDCRequestContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public boolean hasToken() {
        return OIDCValidationContext.class.cast(ctxHolder
                .getRequestAttribute(OIDC_VALIDATION_CONTEXT)) != null;
    }

    public String fnrFromToken() {
        OIDCValidationContext context = (OIDCValidationContext) ctxHolder
                .getRequestAttribute(OIDC_VALIDATION_CONTEXT);
        if (context == null) {
            return "ingen";
        }
        OIDCClaims claims = context.getClaims("selvbetjening");
        if (claims == null) {
            return "ingen";
        }
        JWTClaimsSet claimSet = claims.getClaimSet();
        if (claimSet == null) {
            return "ingen";
        }
        String fnr = claimSet.getSubject();
        if (fnr == null || fnr.trim().isEmpty()) {
            return "ingen";
        }
        return fnr;
    }

}