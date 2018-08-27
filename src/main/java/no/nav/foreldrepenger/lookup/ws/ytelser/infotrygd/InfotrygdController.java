package no.nav.foreldrepenger.lookup.ws.ytelser.infotrygd;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.lookup.FnrExtractor;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
class InfotrygdController {

    private final InfotrygdClient infotrygdClient;
    private final OIDCRequestContextHolder contextHolder;

    @Inject
    public InfotrygdController(InfotrygdClient infotrygdClient, OIDCRequestContextHolder contextHolder) {
        this.infotrygdClient = infotrygdClient;
        this.contextHolder = contextHolder;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/infotrygd")
    public ResponseEntity<List<Ytelse>> benefits() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return badRequest().build();
        }

        Fødselsnummer fnr = new Fødselsnummer(fnrFromClaims);
        return ok(infotrygdClient.casesFor(fnr, LocalDate.now().minusMonths(12), LocalDate.now()));

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [infotrygdClient=" + infotrygdClient + "]";
    }
}
