package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.arena;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.lookup.FnrExtractor;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.Ytelse;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
class ArenaController {

    private final ArenaClient arenaClient;

    private final OIDCRequestContextHolder contextHolder;

    public ArenaController(ArenaClient arenaClient, OIDCRequestContextHolder contextHolder) {
        this.arenaClient = arenaClient;
        this.contextHolder = contextHolder;
    }

    @GetMapping(value = "/arena")
    public ResponseEntity<List<Ytelse>> benefits() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return badRequest().build();
        }

        Fødselsnummer fnr = new Fødselsnummer(fnrFromClaims);
        return ok(arenaClient.ytelser(fnr, LocalDate.now().minusMonths(12), LocalDate.now()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [arenaClient=" + arenaClient + ", contextHolder=" + contextHolder + "]";
    }
}
