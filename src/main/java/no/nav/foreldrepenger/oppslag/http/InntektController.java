package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.http.util.FnrExtractor;
import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;
import no.nav.security.oidc.filter.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDate;

@RestController
@ProtectedWithClaims(issuer="selvbetjening", claimMap={"acr=Level4"})
class InntektController {

    @Inject
    private InntektClient inntektClient;

    @Inject
    private OIDCRequestContextHolder contextHolder;

    @RequestMapping(method = { RequestMethod.GET }, value = "/income")
    public ResponseEntity<?> income() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        LocalDate tenMonthsAgo = LocalDate.now().minusMonths(10);
        return ResponseEntity.ok(inntektClient.incomeForPeriod(fnr, tenMonthsAgo, LocalDate.now()));
    }
}
