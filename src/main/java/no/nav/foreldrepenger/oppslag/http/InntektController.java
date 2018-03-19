package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.spring.oidc.validation.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDate;

@RestController
class InntektController {

    @Inject
    private InntektClient inntektClient;

    @Inject
    private OIDCValidationContext oidcCtx;

    @RequestMapping(method = { RequestMethod.GET }, value = "/income")
    @Protected
    public ResponseEntity<?> income() {
        String fnrFromClaims = oidcCtx.getClaims("selvbetjening").getClaimSet().getSubject();
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        LocalDate tenMonthsAgo = LocalDate.now().minusMonths(10);
        return ResponseEntity.ok(inntektClient.incomeForPeriod(fnr, tenMonthsAgo, LocalDate.now()));
    }
}
