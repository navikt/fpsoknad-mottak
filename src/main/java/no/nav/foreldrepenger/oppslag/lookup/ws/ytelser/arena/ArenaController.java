package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.arena;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.lookup.FnrExtractor;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer="selvbetjening", claimMap={"acr=Level4"})
class ArenaController {

    @Inject
    private ArenaClient arenaClient;

    @Inject
    private OIDCRequestContextHolder contextHolder;

    @RequestMapping(method = { RequestMethod.GET }, value = "/arena")
    public ResponseEntity<List<Ytelse>> benefits() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = LocalDate.now().minusMonths(12);
        return ResponseEntity.ok(arenaClient.ytelser(fnr, oneYearAgo, now));
    }
}
