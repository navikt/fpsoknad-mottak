package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.dokmot.DokmotJMSSender;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;

@Deprecated
@RestController
@RequestMapping(path = MottakDokmotController.DOKMOT, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class MottakDokmotController {

    public static final String DOKMOT = "/mottak/dokmot";

    private final DokmotJMSSender sender;

    public MottakDokmotController(DokmotJMSSender sender) {
        this.sender = sender;
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Kvittering> send(@Valid @RequestBody Søknad søknad) {
        return ok(sender.sendSøknad(søknad, null));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + sender + "]";
    }
}
