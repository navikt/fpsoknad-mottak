package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import javax.validation.Valid;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelSøknadGenerator;
import no.nav.security.spring.oidc.validation.api.Unprotected;

@Unprotected
@RestController
@RequestMapping(path = FPFordelMottakPreprodController.DOKMOT_FPFORDEL)
@Profile("preprod")
public class FPFordelMottakPreprodController {

    public static final String DOKMOT_FPFORDEL = "/mottak/fpfordel/preprod";

    private final FPFordelSøknadGenerator søknadGenerator;
    private final FPFordelKonvoluttGenerator konvoluttGenerator;

    public FPFordelMottakPreprodController(FPFordelSøknadGenerator søknadGenerator,
            FPFordelKonvoluttGenerator konvoluttGenerator) {
        this.søknadGenerator = søknadGenerator;
        this.konvoluttGenerator = konvoluttGenerator;
    }

    @PostMapping(value = "/konvolutt", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpEntity<MultiValueMap<String, HttpEntity<?>>>> konvolutt(
            @Valid @RequestBody Søknad søknad) {
        return ok().body(konvoluttGenerator.payload(søknad, new AktorId("42"), "999"));
    }

    @PostMapping(value = "/søknad", produces = APPLICATION_XML_VALUE)
    public ResponseEntity<String> søknad(@Valid @RequestBody Søknad søknad) {
        return ok().body(søknadGenerator.toXML(søknad, new AktorId("42")));
    }

}
