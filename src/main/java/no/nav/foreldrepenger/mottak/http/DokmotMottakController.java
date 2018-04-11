package no.nav.foreldrepenger.mottak.http;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.security.spring.oidc.validation.api.Unprotected;

@RestController

@RequestMapping(path = DokmotMottakController.DOKMOT, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class DokmotMottakController {

    private static final Logger LOG = getLogger(DokmotMottakController.class);

    public static final String DOKMOT = "/mottak/dokmot";

    private final SøknadSender sender;

    public DokmotMottakController(SøknadSender sender) {
        this.sender = sender;
    }

    @GetMapping(value = "/ping")
    @Unprotected
    public ResponseEntity<String> ping(@RequestParam(name = "navn", defaultValue = "earthling") String navn) {
        LOG.info("Jeg ble pinged");
        return ResponseEntity.ok("Hallo " + navn + " fra ubeskyttet ressurs");
    }

    @GetMapping(value = "/ping1")
    public ResponseEntity<String> ping1(@RequestParam(name = "navn", defaultValue = "earthling") String navn) {
        LOG.info("Jeg ble pinged");
        return ResponseEntity.ok("Hallo " + navn + " fra beskyttet ressurs");
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Kvittering> send(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.ok(sender.sendSøknad(søknad));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + sender + "]";
    }
}
