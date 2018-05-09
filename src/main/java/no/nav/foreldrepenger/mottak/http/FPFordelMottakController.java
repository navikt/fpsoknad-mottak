package no.nav.foreldrepenger.mottak.http;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelConfig;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelSøknadSender;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.security.spring.oidc.validation.api.Unprotected;

@RestController
@RequestMapping(path = FPFordelMottakController.FPFORDEL, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class FPFordelMottakController {

    @Autowired
    private FPFordelConfig cfg;

    private static final Logger LOG = getLogger(FPFordelMottakController.class);

    public static final String FPFORDEL = "/mottak/fpfordel";

    private final FPFordelSøknadSender sender;

    public FPFordelMottakController(FPFordelSøknadSender sender) {
        this.sender = sender;
    }

    @GetMapping(value = "/ping")
    @Unprotected
    public ResponseEntity<String> ping(@RequestParam(name = "navn", defaultValue = "earthling") String navn) {
        LOG.info("Jeg ble pinget {}", cfg.getUri());
        return ok("Hallo " + navn + " fra ubeskyttet ressurs");
    }

    @GetMapping(value = "/ping1")
    public ResponseEntity<String> ping1(@RequestParam(name = "navn", defaultValue = "earthling") String navn) {
        LOG.info("Jeg ble pinget");
        return ok("Hallo " + navn + " fra beskyttet ressurs");
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Kvittering> send(@Valid @RequestBody Søknad søknad) {
        return ok(sender.sendSøknad(søknad));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + sender + "]";
    }
}
