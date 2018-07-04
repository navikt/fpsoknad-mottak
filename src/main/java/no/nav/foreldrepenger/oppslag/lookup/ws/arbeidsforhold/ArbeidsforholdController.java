package no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.lookup.FnrExtractor;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer="selvbetjening", claimMap={"acr=Level4"})
class ArbeidsforholdController {

    @Inject
    private ArbeidsforholdClient arbeidsforholdClient;

    @Inject
    private OIDCRequestContextHolder contextHolder;

    @RequestMapping(method = { RequestMethod.GET }, value = "/arbeidsforhold")
    public ResponseEntity<List<Arbeidsforhold>> workHistory() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fødselsnummer fnr = new Fødselsnummer(fnrFromClaims);
        List<Arbeidsforhold> arbeidsforhold = arbeidsforholdClient.arbeidsforhold(fnr);
        return ResponseEntity.ok(arbeidsforhold);
    }
}
