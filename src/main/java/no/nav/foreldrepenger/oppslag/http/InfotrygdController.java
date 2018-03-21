package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdClient;
import no.nav.security.oidc.OIDCConstants;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.filter.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@RestController
class InfotrygdController {

    @Inject
    private InfotrygdClient infotrygdClient;

    @Inject
    private OIDCRequestContextHolder contextHolder;

    @RequestMapping(method = { RequestMethod.GET }, value = "/infotrygd")
    @Protected
    public ResponseEntity<List<Ytelse>> benefits() {
        OIDCValidationContext context = (OIDCValidationContext) contextHolder
            .getRequestAttribute(OIDCConstants.OIDC_VALIDATION_CONTEXT);
        String fnrFromClaims = context.getClaims("selvbetjening").getClaimSet().getSubject();
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = LocalDate.now().minusMonths(12);
        return ResponseEntity.ok(infotrygdClient.casesFor(fnr, oneYearAgo, now));

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [infotrygdClient=" + infotrygdClient + "]";
    }
}
