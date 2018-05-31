package no.nav.foreldrepenger.mottak.http;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotJMSSender;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.security.spring.oidc.validation.api.Unprotected;

@Unprotected
@RestController
@RequestMapping(path = DokmotMottakPreprodController.DOKMOT_PREPROD, produces = APPLICATION_XML_VALUE)
@Profile("preprod")
public class DokmotMottakPreprodController {

    public static final String DOKMOT_PREPROD = "/mottak/preprod";
    private static final Logger LOG = getLogger(DokmotMottakPreprodController.class);

    private final DokmotJMSSender sender;
    private final DokmotEngangsstønadXMLGenerator søknadGenerator;
    private final DokmotEngangsstønadXMLKonvoluttGenerator konvoluttGenerator;

    public DokmotMottakPreprodController(DokmotJMSSender sender, DokmotEngangsstønadXMLGenerator søknadGenerator,
            DokmotEngangsstønadXMLKonvoluttGenerator konvoluttGenerator) {
        this.sender = sender;
        this.søknadGenerator = søknadGenerator;
        this.konvoluttGenerator = konvoluttGenerator;
    }

    @GetMapping(value = "/ping", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> unsecuredPing(@RequestParam("navn") String navn) {
        LOG.info("I was unprotected and pinged");
        return ok().body("Unprotected hello " + navn);
    }

    @PostMapping("/søknad")
    public ResponseEntity<String> søknad(@Valid @RequestBody Søknad søknad) {
        return ok().body(søknadGenerator.toXML(søknad));
    }

    @PostMapping("/konvolutt")
    public ResponseEntity<String> konvolutt(@Valid @RequestBody Søknad søknad) {
        return ok().body(konvoluttGenerator.toXML(søknad, UUID.randomUUID().toString()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + sender + ", søknadGenerator=" + søknadGenerator
                + ", konvoluttGenerator=" + konvoluttGenerator + "]";
    }

}
