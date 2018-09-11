package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import no.nav.foreldrepenger.lookup.FnrExtractor;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
class GsakController {

    private final GsakClient gsakClient;
    private final OIDCRequestContextHolder contextHolder;

    @Inject
    public GsakController(GsakClient gsakClient, OIDCRequestContextHolder contextHolder) {
        this.gsakClient = gsakClient;
        this.contextHolder = contextHolder;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/gsak")
    public ResponseEntity<List<Ytelse>> cases() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return badRequest().build();
        }

        Fødselsnummer fnr = new Fødselsnummer(fnrFromClaims);
        return ok(gsakClient.casesFor(fnr));

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [gsakClient=" + gsakClient + "]";
    }
}
