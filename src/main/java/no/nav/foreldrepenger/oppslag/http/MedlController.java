package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.MedlPeriode;
import no.nav.foreldrepenger.oppslag.http.util.FnrExtractor;
import no.nav.foreldrepenger.oppslag.medl.MedlClient;
import no.nav.security.oidc.filter.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
class MedlController {

    @Inject
    private MedlClient medlClient;

    @Inject
    private OIDCRequestContextHolder contextHolder;

    @RequestMapping(method = { RequestMethod.GET }, value = "/medl")
    @Protected
    public ResponseEntity<List<MedlPeriode>> membership() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        return ResponseEntity.ok(medlClient.medlInfo(fnr));
    }
}
