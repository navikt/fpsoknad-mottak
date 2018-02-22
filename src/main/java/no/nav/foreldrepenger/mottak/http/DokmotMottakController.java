package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotJMSSender;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;

@RestController
@RequestMapping(DokmotMottakController.DOKMOT)
public class DokmotMottakController {

    public static final String DOKMOT = "/mottak/dokmot";

    private final DokmotJMSSender sender;
    @Autowired
    DokmotEngangsstønadXMLGenerator søknadGenerator;
    @Autowired
    DokmotEngangsstønadXMLKonvoluttGenerator konvoluttGenerator;

    @Inject
    public DokmotMottakController(DokmotJMSSender sender) {
        this.sender = sender;
    }

    @PostMapping(value = "/søknad", produces = APPLICATION_XML_VALUE)
    public ResponseEntity<String> søknad(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(søknadGenerator.toXML(søknad));
    }

    @PostMapping(value = "/konvolutt", produces = APPLICATION_XML_VALUE)
    public ResponseEntity<String> konvolutt(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(konvoluttGenerator.toXML(søknad));
    }

    @PostMapping(value = "/model", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SoeknadsskjemaEngangsstoenad> model(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(søknadGenerator.toDokmotModel(søknad));
    }

    @PostMapping(value = "/send", produces = APPLICATION_XML_VALUE)
    public ResponseEntity<String> send(@Valid @RequestBody Søknad søknad) {
        sender.sendSøknad(søknad);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + sender + "]";
    }
}
