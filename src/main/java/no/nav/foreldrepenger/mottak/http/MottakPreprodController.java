package no.nav.foreldrepenger.mottak.http;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
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
        return isForeldrepenger(søknad) ? ok().body(fpsøknad(søknad)) : ok().body(essøknad(søknad, søker()));
    }

    private String essøknad(Søknad søknad, Person søker) {
        return dokmotSøknadGenerator.toXML(søknad, søker);
    }

    private String fpsøknad(Søknad søknad) {
        return fpfordelSøknadGenerator.toXML(søknad, new AktorId("42"));
    }

    @PostMapping(path = "/konvolutt", produces = { APPLICATION_XML_VALUE, APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> konvolutt(@Valid @RequestBody Søknad søknad) {
        return isForeldrepenger(søknad) ? ok().body(fpKonvolutt(søknad, søker()))
                : ok().body(eskonvolutt(søknad, søker()));
    }

    private String eskonvolutt(Søknad søknad, Person søker) {
        return dokmotKonvoluttGenerator.toXML(søknad, søker, UUID.randomUUID().toString());
    }

    private Object fpKonvolutt(Søknad søknad, Person søker) {
        return fpfordelKonvoluttGenerator.payload(søknad, søker, "999");
    }

    private static boolean isForeldrepenger(Søknad søknad) {
        return søknad.getYtelse() instanceof Foreldrepenger;
    }

    private static Person søker() {
        Person søker = new Person();
        søker.aktørId = new AktorId("42");
        søker.bankkonto = new Bankkonto("2000.20.20000", "Store Fiskerbank");
        søker.fnr = new Fødselsnummer("010101010101");
        søker.fornavn = "Mor";
        søker.mellomnavn = "Mellommor";
        søker.etternavn = "Moro";
        søker.fødselsdato = LocalDate.now().minusYears(25);
        søker.kjønn = "K";
        søker.ikkeNordiskEøsLand = false;
        søker.land = CountryCode.NO;
        søker.målform = "NN";
        return søker;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [søknadGenerator=" + dokmotSøknadGenerator + ", konvoluttGenerator="
                + dokmotKonvoluttGenerator + "]";
    }

}
