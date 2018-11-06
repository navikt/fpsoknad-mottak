package no.nav.foreldrepenger.lookup.rest.sak;

import static org.springframework.http.ResponseEntity.ok;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.lookup.TokenHandler;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.security.oidc.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class SakController {

    public static final String SAK = "/sak";
    private final SakClient sakClient;
    private final AktorIdClient aktorClient;
    private final TokenHandler tokenHandler;

    @Inject
    public SakController(SakClient sakClient, AktorIdClient aktorClient, TokenHandler tokenHandler) {
        this.sakClient = sakClient;
        this.tokenHandler = tokenHandler;
        this.aktorClient = aktorClient;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = SAK)
    public ResponseEntity<List<Sak>> cases() {
        String oidcToken = tokenHandler.getToken();
        Fødselsnummer fnr = tokenHandler.autentisertBruker();
        final AktorId aktorId = aktorClient.aktorIdForFnr(fnr);
        return ok(sakClient.sakerFor(aktorId, oidcToken));

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sakClient=" + sakClient + "]";
    }
}
