package no.nav.foreldrepenger.mottak.http;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotJMSSender;
import no.nav.foreldrepenger.mottak.domain.Søknad;

@RestController
class DokmotMottakController {

    private final DokmotJMSSender sender;
    @Autowired
    DokmotEngangsstønadXMLGenerator søknadGenerator;
    @Autowired
    DokmotEngangsstønadXMLKonvoluttGenerator konvoluttGenerator;

    @Inject
    public DokmotMottakController(DokmotJMSSender sender) {
        this.sender = sender;
    }

    @PostMapping(value = "/mottak/dokmot/søknad", produces = { "application/xml" })
    public ResponseEntity<String> mottak(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(søknadGenerator.toXML(søknad));
    }

    @PostMapping(value = "/mottak/dokmot/konvolutt", produces = { "application/xml" })
    public ResponseEntity<String> mottakDokmotPing(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(konvoluttGenerator.toXML(søknad));
    }

    @PostMapping(value = "/mottak/dokmot/send", produces = { "application/json" })
    public ResponseEntity<String> mottakDokmotSend(@Valid @RequestBody Søknad søknad) {
        sender.sendSøknad(søknad);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + sender + "]";
    }
}
