package no.nav.foreldrepenger.mottak.http;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Søknad;

@RestController
class MottakController {

    // private final XMLEnvelopeGenerator dokmotXmlGenerator;

    /* @Inject public MottakController(XMLEnvelopeGenerator xmlGenerator) { this.dokmotXmlGenerator = xmlGenerator; } */

    @PostMapping(value = "/mottak", produces = { "application/xml" })
    public ResponseEntity<String> mottak(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(/* dokmotXmlGenerator.getGenerator().toXML(søknad) */"OK");
    }

    @PostMapping(value = "/mottak/dokmot", produces = { "application/xml" })
    public ResponseEntity<String> mottakDokmot(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(/* dokmotXmlGenerator.toXML(søknad) */"OK");
    }

    @PostMapping(value = "/mottak/ping", produces = { "application/xml" })
    public ResponseEntity<String> ping() {
        return ResponseEntity.status(HttpStatus.OK).body("PONG");
    }

}
