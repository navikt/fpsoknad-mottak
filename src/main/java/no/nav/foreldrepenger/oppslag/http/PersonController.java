package no.nav.foreldrepenger.oppslag.http;

import static org.springframework.http.HttpStatus.OK;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Person;
import no.nav.foreldrepenger.oppslag.person.PersonClient;

@RestController
@Validated
@RequestMapping("/person")
public class PersonController {

    @Inject
    private AktorIdClient aktorClient;
    @Inject
    private PersonClient personClient;

    @GetMapping(value = "/")
    public ResponseEntity<Person> person(@Valid @RequestParam(value = "fnr", required = true) Fodselsnummer fnr) {
        Person person = personClient.hentPersonInfo(new ID(aktorClient.aktorIdForFnr(fnr), fnr));
        return new ResponseEntity<>(person, OK);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient + "]";
    }

}
