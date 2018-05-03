package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import no.nav.foreldrepenger.oppslag.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.lookup.FnrExtractor;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static org.springframework.http.HttpStatus.OK;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = {"acr=Level4"})
@RequestMapping("/person")
public class PersonController {

    @Inject
    private AktorIdClient aktorClient;

    @Inject
    private PersonClient personClient;

    @Inject
    private OIDCRequestContextHolder contextHolder;

    @GetMapping
    public ResponseEntity<Person> person() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        return new ResponseEntity<>(personClient.hentPersonInfo(new ID(aktorClient.aktorIdForFnr(fnr), fnr)), OK);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient + "]";
    }
}
