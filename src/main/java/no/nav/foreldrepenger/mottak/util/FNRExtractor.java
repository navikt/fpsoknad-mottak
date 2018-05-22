package no.nav.foreldrepenger.mottak.util;

import static no.nav.security.oidc.OIDCConstants.OIDC_VALIDATION_CONTEXT;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;

public class FNRExtractor {

    private FNRExtractor() {

    }

    public static String userFromContext(OIDCRequestContextHolder ctxHolder) {
        return OIDCValidationContext.class.cast(ctxHolder.getRequestAttribute(OIDC_VALIDATION_CONTEXT))
                .getClaims("selvbetjening").getClaimSet().getSubject();
    }

}