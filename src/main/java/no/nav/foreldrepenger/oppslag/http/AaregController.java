package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.aareg.AaregClient;
import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.spring.oidc.validation.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
class AaregController {

    @Inject
    private AaregClient aaregClient;

    @Inject
    private OIDCValidationContext oidcCtx;

    @RequestMapping(method = { RequestMethod.GET }, value = "/aareg")
    @Protected
    public ResponseEntity<List<Arbeidsforhold>> workHistory() {
        String fnrFromClaims = oidcCtx.getClaims("selvbetjening").getClaimSet().getSubject();
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        List<Arbeidsforhold> arbeidsforhold = aaregClient.arbeidsforhold(fnr);
        return ResponseEntity.ok(arbeidsforhold);
    }
}
