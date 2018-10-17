package no.nav.foreldrepenger.lookup.rest.sak;

import no.nav.foreldrepenger.lookup.FnrExtractor;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.security.oidc.OIDCConstants;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;
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
class SakController {

    private final SakClient sakClient;
    private final AktorIdClient aktorClient;
    private final OIDCRequestContextHolder contextHolder;

    @Inject
    public SakController(SakClient sakClient, AktorIdClient aktorClient, OIDCRequestContextHolder contextHolder) {
        this.sakClient = sakClient;
        this.contextHolder = contextHolder;
        this.aktorClient = aktorClient;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/sak")
    public ResponseEntity<List<Sak>> cases() {
        String oidcToken = ((OIDCValidationContext) contextHolder.getRequestAttribute(
            OIDCConstants.OIDC_VALIDATION_CONTEXT)).getToken("selvbetjening").getIdToken();
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return badRequest().build();
        }

        Fødselsnummer fnr = new Fødselsnummer(fnrFromClaims);
        final AktorId aktorId = aktorClient.aktorIdForFnr(fnr);
        return ok(sakClient.sakerFor(aktorId, oidcToken));

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sakClient=" + sakClient + "]";
    }
}
