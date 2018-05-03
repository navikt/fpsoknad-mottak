package no.nav.foreldrepenger.oppslag.lookup;

import no.nav.security.oidc.OIDCConstants;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.context.OIDCRequestContextHolder;

public class FnrExtractor {

    public static String extract(OIDCRequestContextHolder ctxHolder) {
        OIDCValidationContext context = (OIDCValidationContext) ctxHolder
            .getRequestAttribute(OIDCConstants.OIDC_VALIDATION_CONTEXT);
        return context.getClaims("selvbetjening").getClaimSet().getSubject();
    }

}
