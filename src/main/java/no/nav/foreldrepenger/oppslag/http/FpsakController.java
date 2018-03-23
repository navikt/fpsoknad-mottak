package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakClient;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@ProtectedWithClaims(issuer="selvbetjening", claimMap={"acr=Level4"})
class FpsakController {

    private final FpsakClient fpsakClient;

    @Inject
    public FpsakController(FpsakClient fpsakClient) {
        this.fpsakClient = fpsakClient;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/fpsak")
    public ResponseEntity<List<Ytelse>> existingCases(@Valid @RequestParam("aktør") AktorId aktor) {
        return ResponseEntity.ok(fpsakClient.casesFor(aktor));
    }
}
