package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Person;
import no.nav.foreldrepenger.oppslag.person.PersonClient;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.spring.oidc.validation.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Validated
@RequestMapping("/person")
public class PersonController {

    @Inject
    private AktorIdClient aktorClient;
    @Inject
    private PersonClient personClient;
    @Inject
    private OIDCValidationContext oidcCtx;

    @GetMapping
    @Protected
    public ResponseEntity<Person> person() {
        String fnrFromClaims = oidcCtx.getClaims("selvbetjening").getClaimSet().getSubject();
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
