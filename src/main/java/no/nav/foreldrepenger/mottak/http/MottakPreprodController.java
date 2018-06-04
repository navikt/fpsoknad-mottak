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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelSøknadGenerator;
import no.nav.security.spring.oidc.validation.api.Unprotected;

@Unprotected
@RestController
@RequestMapping(path = MottakPreprodController.INNSENDING_PREPROD, produces = APPLICATION_XML_VALUE)
@Profile("preprod")
public class MottakPreprodController {

    public static final String INNSENDING_PREPROD = "/mottak/preprod";
    private static final Logger LOG = getLogger(MottakPreprodController.class);

    private final DokmotEngangsstønadXMLGenerator dokmotSøknadGenerator;
    private final DokmotEngangsstønadXMLKonvoluttGenerator dokmotKonvoluttGenerator;
    private final FPFordelSøknadGenerator fpfordelSøknadGenerator;
    private final FPFordelKonvoluttGenerator fpfordelKonvoluttGenerator;

    public MottakPreprodController(DokmotEngangsstønadXMLGenerator dokmotSøknadGenerator,
            DokmotEngangsstønadXMLKonvoluttGenerator dokmotKonvoluttGenerator,
            FPFordelSøknadGenerator fpfordelSøknadGenerator, FPFordelKonvoluttGenerator fpfordelKonvoluttGenerator) {
        this.dokmotSøknadGenerator = dokmotSøknadGenerator;
        this.dokmotKonvoluttGenerator = dokmotKonvoluttGenerator;
        this.fpfordelSøknadGenerator = fpfordelSøknadGenerator;
        this.fpfordelKonvoluttGenerator = fpfordelKonvoluttGenerator;
    }

    @PostMapping("/søknad")
    public ResponseEntity<String> søknad(@Valid @RequestBody Søknad søknad) {
        return isForeldrepenger(søknad) ? ok().body(fpsøknad(søknad)) : ok().body(essøknad(søknad));
    }

    private String essøknad(Søknad søknad) {
        return dokmotSøknadGenerator.toXML(søknad);
    }

    private String fpsøknad(Søknad søknad) {
        return fpfordelSøknadGenerator.toXML(søknad, new AktorId("42"));
    }

    @PostMapping(path = "/konvolutt", produces = { APPLICATION_XML_VALUE, APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> konvolutt(@Valid @RequestBody Søknad søknad) {
        return isForeldrepenger(søknad) ? ok().body(fpKonvolutt(søknad)) : ok().body(eskonvolutt(søknad));
    }

    private String eskonvolutt(Søknad søknad) {
        return dokmotKonvoluttGenerator.toXML(søknad, UUID.randomUUID().toString());
    }

    private Object fpKonvolutt(Søknad søknad) {
        return fpfordelKonvoluttGenerator.payload(søknad, new AktorId("42"), "999");
    }

    private static boolean isForeldrepenger(Søknad søknad) {
        return søknad.getYtelse() instanceof Foreldrepenger;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [søknadGenerator=" + dokmotSøknadGenerator + ", konvoluttGenerator="
                + dokmotKonvoluttGenerator + "]";
    }

}
