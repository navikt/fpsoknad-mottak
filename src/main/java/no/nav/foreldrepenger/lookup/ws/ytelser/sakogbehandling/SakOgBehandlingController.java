package no.nav.foreldrepenger.lookup.ws.ytelser.sakogbehandling;

import no.nav.foreldrepenger.lookup.FnrExtractor;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
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
class SakOgBehandlingController {

    private final SakOgBehandlingClient sakOgBehandlingClient;
    private final AktorIdClient aktorClient;
    private final OIDCRequestContextHolder contextHolder;

    @Inject
    public SakOgBehandlingController(SakOgBehandlingClient sakOgBehandlingClient,
                                     AktorIdClient aktorClient,
                                     OIDCRequestContextHolder contextHolder) {
        this.sakOgBehandlingClient = sakOgBehandlingClient;
        this.aktorClient = aktorClient;
        this.contextHolder = contextHolder;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/sakogbehandling")
    public ResponseEntity<List<Sak>> cases() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return badRequest().build();
        }

        Fødselsnummer fnr = new Fødselsnummer(fnrFromClaims);
        AktorId aktorId = aktorClient.aktorIdForFnr(fnr);
        return ok(sakOgBehandlingClient.casesFor(aktorId));

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sakOgBehandlingClient=" + sakOgBehandlingClient + "]";
    }
}
