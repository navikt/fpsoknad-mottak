package no.nav.foreldrepenger.mottak.http;

import no.nav.security.oidc.OIDCConstants;
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
        String fnr = context.getClaims("selvbetjening").getClaimSet().getSubject();
        if (fnr == null || fnr.trim().isEmpty()) {
            return "ukjent";
        }
        return fnr;
    }

}