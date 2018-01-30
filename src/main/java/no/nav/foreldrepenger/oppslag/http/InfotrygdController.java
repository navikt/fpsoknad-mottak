package no.nav.foreldrepenger.oppslag.http;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdClient;

@RestController
class InfotrygdController {

    private final InfotrygdClient infotrygdClient;

    @Inject
    public InfotrygdController(InfotrygdClient infotrygdClient) {
        this.infotrygdClient = infotrygdClient;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/infotrygd")
    public ResponseEntity<List<Ytelse>> casesFor(@RequestParam("fnr") Fodselsnummer fnr) {
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = LocalDate.now().minusMonths(12);
        return ResponseEntity.ok(infotrygdClient.casesFor(fnr, oneYearAgo, now));

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [infotrygdClient=" + infotrygdClient + "]";
    }
}
