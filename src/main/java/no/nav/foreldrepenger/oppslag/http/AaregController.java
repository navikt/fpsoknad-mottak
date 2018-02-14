package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.aareg.AaregClient;
import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
class AaregController {

    private final AaregClient aaregClient;

    @Inject
    public AaregController(AaregClient aaregClient) {
        this.aaregClient = aaregClient;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/aareg")
    public ResponseEntity<List<Arbeidsforhold>> incomeForAktor(@RequestParam("fnr") Fodselsnummer fnr) {
       List<Arbeidsforhold> arbeidsforhold = aaregClient.arbeidsforhold(fnr);
       return ResponseEntity.ok(arbeidsforhold);
    }
}
