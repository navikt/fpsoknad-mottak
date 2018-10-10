package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDate;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.innsending.engangsstønad.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.innsending.engangsstønad.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadTilXMLMapper;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengerPDFGenerator;
import no.nav.security.oidc.api.Unprotected;

@Unprotected
@RestController
@RequestMapping(path = SøknadPreprodController.INNSENDING_PREPROD, produces = APPLICATION_XML_VALUE)
@Profile(PREPROD)
public class SøknadPreprodController {

    public static final String INNSENDING_PREPROD = "/preprod";

    private final DokmotEngangsstønadXMLGenerator dokmotSøknadGenerator;
    private final DokmotEngangsstønadXMLKonvoluttGenerator dokmotKonvoluttGenerator;
    private final SøknadTilXMLMapper søknadMapper;
    private final FPFordelKonvoluttGenerator fpfordelKonvoluttGenerator;
    @Inject
    ForeldrepengerPDFGenerator pdfGenerator;

    public SøknadPreprodController(DokmotEngangsstønadXMLGenerator dokmotSøknadGenerator,
            DokmotEngangsstønadXMLKonvoluttGenerator dokmotKonvoluttGenerator,
            SøknadTilXMLMapper søknadMapper, FPFordelKonvoluttGenerator fpfordelKonvoluttGenerator) {
        this.dokmotSøknadGenerator = dokmotSøknadGenerator;
        this.dokmotKonvoluttGenerator = dokmotKonvoluttGenerator;
        this.søknadMapper = søknadMapper;
        this.fpfordelKonvoluttGenerator = fpfordelKonvoluttGenerator;
    }

    @PostMapping("/søknad")
    public ResponseEntity<String> søknad(@Valid @RequestBody Søknad søknad) {
        return isForeldrepenger(søknad)
                ? ok().body(fpSøknad(søknad))
                : ok().body(esSøknad(søknad, søker()));
    }

    @PostMapping("/endringssøknad")
    public ResponseEntity<String> endringssøknad(@Valid @RequestBody Endringssøknad endringssøknad) {
        return isForeldrepenger(endringssøknad)
                ? ok().body(fpEndringsSøknad(endringssøknad))
                : ok().body(esSøknad(endringssøknad, søker()));
    }

    @PostMapping(path = "/pdfEndring", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdfEndring(@Valid @RequestBody Endringssøknad endringssøknad) {
        return ok()
                .header("Content-disposition", "attachment; filename=" + endringssøknad.getSaksnr())
                .body(pdfGenerator.generate(endringssøknad, søker(), false));
    }

    @PostMapping(path = "/pdfSøknad", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdfSøknad(@Valid @RequestBody Søknad søknad) {
        return ok()
                .header("Content-disposition", "attachment; filename=søknad")
                .body(pdfGenerator.generate(søknad, søker(), false));
    }

    @PostMapping(path = "/konvolutt", produces = { APPLICATION_XML_VALUE, APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> konvolutt(@Valid @RequestBody Søknad søknad) {
        return isForeldrepenger(søknad)
                ? ok().body(fpKonvolutt(søknad, søker()))
                : ok().body(esKonvolutt(søknad, søker()));
    }

    private String esSøknad(Søknad søknad, Person søker) {
        return dokmotSøknadGenerator.tilXML(søknad, søker);
    }

    private String fpSøknad(Søknad søknad) {
        return søknadMapper.tilXML(søknad, new AktorId("42"), false);
    }

    private String fpEndringsSøknad(Endringssøknad endringssøknad) {
        return søknadMapper.tilXML(endringssøknad, new AktorId("42"), false);
    }

    private String esKonvolutt(Søknad søknad, Person søker) {
        return dokmotKonvoluttGenerator.tilXML(søknad, søker, UUID.randomUUID().toString());
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
        søker.mellomnavn = "Godhjerta";
        søker.etternavn = "Morssom";
        søker.fødselsdato = LocalDate.now().minusYears(25);
        søker.kjønn = "K";
        søker.ikkeNordiskEøsLand = false;
        søker.land = CountryCode.NO;
        søker.målform = "NN";
        return søker;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotSøknadGenerator=" + dokmotSøknadGenerator
                + ", dokmotKonvoluttGenerator=" + dokmotKonvoluttGenerator + ", søknadMapper="
                + søknadMapper + ", fpfordelKonvoluttGenerator=" + fpfordelKonvoluttGenerator + "]";
    }
}
