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

    @PostMapping(value = "/mottak", produces = { "application/json", "application/xml" })
    public ResponseEntity<Søknad> mottak(@Valid @RequestBody Søknad soknad) {
        return ResponseEntity.status(HttpStatus.OK).body(soknad);
    }
}
