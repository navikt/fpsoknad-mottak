package no.nav.foreldrepenger.mottak.http;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.domain.Søknad;

@RestController
class MottakController {

    private final DokmotEngangsstønadXMLGenerator dokmotXmlGenerator;

    @Inject
    public MottakController(DokmotEngangsstønadXMLGenerator xmlGenerator) {
        this.dokmotXmlGenerator = xmlGenerator;
    }

    @PostMapping(value = "/mottak", produces = { "application/json" })
    public ResponseEntity<Søknad> mottak(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(søknad);
    }

    @PostMapping(value = "/mottak/dokmot", produces = { "application/xml" })
    public ResponseEntity<String> mottakDokmot(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(dokmotXmlGenerator.toXML(søknad));
    }

}
