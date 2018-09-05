package no.nav.foreldrepenger.mottak.util;

import com.nimbusds.jwt.JWTClaimsSet;

import no.nav.security.oidc.OIDCConstants;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;

public final class FnrExtractor {

    private final OIDCRequestContextHolder ctxHolder;

    public FnrExtractor(OIDCRequestContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public String fnrFromToken() {
        OIDCValidationContext context = (OIDCValidationContext) ctxHolder
                .getRequestAttribute(OIDCConstants.OIDC_VALIDATION_CONTEXT);
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