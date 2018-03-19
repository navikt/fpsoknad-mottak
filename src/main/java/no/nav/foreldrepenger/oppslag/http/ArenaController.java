package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.arena.ArenaClient;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.spring.oidc.validation.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@RestController
class ArenaController {

    @Inject
    private ArenaClient arenaClient;

    @Inject
    private OIDCValidationContext oidcCtx;

    @RequestMapping(method = { RequestMethod.GET }, value = "/arena")
    @Protected
    public ResponseEntity<List<Ytelse>> benefits() {
        String fnrFromClaims = oidcCtx.getClaims("selvbetjening").getClaimSet().getSubject();
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = LocalDate.now().minusMonths(12);
        return ResponseEntity.ok(arenaClient.ytelser(fnr, oneYearAgo, now));
    }
}
