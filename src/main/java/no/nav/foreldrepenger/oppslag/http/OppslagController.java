package no.nav.foreldrepenger.oppslag.http;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import javax.inject.Inject;
import javax.validation.Valid;

import no.nav.security.spring.oidc.validation.api.Protected;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.domain.AggregatedLookupResults;
import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Person;
import no.nav.foreldrepenger.oppslag.domain.SøkerInformasjon;
import no.nav.foreldrepenger.oppslag.orchestrate.CoordinatedLookup;
import no.nav.foreldrepenger.oppslag.person.PersonClient;

@RestController
@Validated
@RequestMapping("/oppslag")
public class OppslagController {

    private static final Logger LOG = getLogger(OppslagController.class);

    @Inject
    private PersonClient personClient;
    @Inject
    private AktorIdClient aktorClient;
    @Inject
    private CoordinatedLookup personInfo;

    @GetMapping(value = "/ping", produces = APPLICATION_XML_VALUE)
    public ResponseEntity<String> ping(@RequestParam("navn") String navn) {
        LOG.info("I was pinged");
        return ResponseEntity.status(HttpStatus.OK).body("Hello " + navn);
    }

    @GetMapping(value = "/")
    @Protected
    public ResponseEntity<SøkerInformasjon> oppslag(@Valid @RequestParam(value = "fnr") Fodselsnummer fnr) {
        AktorId aktorid = aktorClient.aktorIdForFnr(fnr);
        Person person = personClient.hentPersonInfo(new ID(aktorid, fnr));
        AggregatedLookupResults results = personInfo.gimmeAllYouGot(new ID(aktorid, fnr));
        return new ResponseEntity<>(
                new SøkerInformasjon(
                        person,
                        results.getInntekt(),
                        results.getYtelser(),
                        results.getArbeidsforhold(),
                        results.getMedlPerioder()),
                OK);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [personClient=" + personClient + ", aktorClient=" + aktorClient
                + ", personInfo=" + personInfo
                + "]";
    }

}
