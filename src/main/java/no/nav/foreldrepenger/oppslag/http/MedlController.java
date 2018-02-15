package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.MedlPeriode;
import no.nav.foreldrepenger.oppslag.medl.MedlClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
class MedlController {

    private final MedlClient medlClient;

    @Inject
    public MedlController(MedlClient medlClient) {
        this.medlClient = medlClient;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/medl")
    public ResponseEntity<List<MedlPeriode>> medlemskap(@RequestParam("fnr") Fodselsnummer fnr) {
       return ResponseEntity.ok(medlClient.medlInfo(fnr));
    }
}
