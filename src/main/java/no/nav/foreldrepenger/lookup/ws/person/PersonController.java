package no.nav.foreldrepenger.lookup.ws.person;

import static org.springframework.http.ResponseEntity.ok;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.lookup.EnvUtil;
import no.nav.foreldrepenger.lookup.FnrExtractor;
import no.nav.foreldrepenger.lookup.rest.fpinfo.SaksStatusService;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.security.oidc.context.OIDCRequestContextHolder;

@RestController
@no.nav.security.oidc.api.ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
@RequestMapping("/person")
public class PersonController {

    @Inject
    SaksStatusService saksClient;

    @Inject
    Environment env;

    private static final Logger LOG = LoggerFactory.getLogger(PersonController.class);
    private final AktorIdClient aktorClient;
    private final PersonClient personClient;
    private final OIDCRequestContextHolder contextHolder;

    @Inject
    public PersonController(AktorIdClient aktorClient, PersonClient personClient,
            OIDCRequestContextHolder contextHolder) {
        this.aktorClient = aktorClient;
        this.personClient = personClient;
        this.contextHolder = contextHolder;
    }

    @GetMapping
    public ResponseEntity<Person> person() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fødselsnummer fnr = new Fødselsnummer(fnrFromClaims);
        AktorId aktorId = aktorClient.aktorIdForFnr(fnr);
        if (EnvUtil.isDevOrPreprod(env)) {
            hentSaker(aktorId);
        }
        return ok(personClient.hentPersonInfo(new ID(aktorId, fnr)));
    }

    private void hentSaker(AktorId aktorId) {
        try {
            LOG.info("Henter saker for {}", aktorId);
            saksClient.hentSaker(aktorId);
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente saker for {}", aktorId, e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient + "]";
    }
}
