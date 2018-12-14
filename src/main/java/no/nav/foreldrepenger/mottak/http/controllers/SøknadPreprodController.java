package no.nav.foreldrepenger.mottak.http.controllers;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.engangsstønad.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.VersjonerbarDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengerPDFGenerator;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.security.oidc.api.Unprotected;

@Unprotected
@RestController
@RequestMapping(path = SøknadPreprodController.INNSENDING_PREPROD, produces = APPLICATION_XML_VALUE)
@Profile(PREPROD)
public class SøknadPreprodController {

    public static final String INNSENDING_PREPROD = "/preprod";

    private final DokmotEngangsstønadXMLGenerator esDomainMapper;
    private final VersjonerbarDomainMapper fpDomainMapper;
    private final ForeldrepengerPDFGenerator pdfGenerator;

    public SøknadPreprodController(VersjonerbarDomainMapper fpDomainMapper,
            DokmotEngangsstønadXMLGenerator esDomainMapper, ForeldrepengerPDFGenerator pdfGenerator) {
        this.fpDomainMapper = fpDomainMapper;
        this.esDomainMapper = esDomainMapper;
        this.pdfGenerator = pdfGenerator;
    }

    @PostMapping("/søknad")
    public String FPsøknadV1(@Valid @RequestBody Søknad søknad) {
        return fpSøknad(søknad, Versjon.V1);
    }

    @PostMapping("/søknadES")
    public String ESsøknad(@Valid @RequestBody Søknad søknad) {
        return esSøknad(søknad);
    }

    @PostMapping("/søknadV2")
    public String FPsøknadV2(@Valid @RequestBody Søknad søknad) {
        return fpSøknad(søknad, Versjon.V2);
    }

    @PostMapping("/endringssøknad")
    public String FPendringssøknadV1(@Valid @RequestBody Endringssøknad endringssøknad) {
        return fpEndringsSøknad(endringssøknad, Versjon.V1);
    }

    @PostMapping("/endringssøknadV2")
    public String FPendringssøknadV2(@Valid @RequestBody Endringssøknad endringssøknad) {
        return fpEndringsSøknad(endringssøknad, Versjon.V2);
    }

    @PostMapping(path = "/pdfEndring", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdfEndring(@Valid @RequestBody Endringssøknad endringssøknad) {
        return ok()
                .header("Content-disposition", "attachment; filename=" + endringssøknad.getSaksnr())
                .body(pdfGenerator.generate(endringssøknad, søker(), arbeidsforhold()));
    }

    @PostMapping(path = "/pdfSøknad", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdfSøknad(@Valid @RequestBody Søknad søknad) {
        return ok()
                .header("Content-disposition", "attachment; filename=søknad")
                .body(pdfGenerator.generate(søknad, søker(), arbeidsforhold()));
    }

    private String fpSøknad(Søknad søknad, Versjon v) {
        return fpDomainMapper.tilXML(søknad, new AktorId("42"), v);
    }

    private String esSøknad(Søknad søknad) {
        return esDomainMapper.tilXML(søknad, søker());
    }

    private String fpEndringsSøknad(Endringssøknad endringssøknad, Versjon v) {
        return fpDomainMapper.tilXML(endringssøknad, new AktorId("42"), v);
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

    private static List<Arbeidsforhold> arbeidsforhold() {
        return Lists.newArrayList(new Arbeidsforhold("1234", "", LocalDate.now().minusDays(200),
                Optional.of(LocalDate.now()), 90.0, "El Bedrifto"),
                new Arbeidsforhold("5678", "", LocalDate.now().minusDays(100),
                        Optional.of(LocalDate.now()), 80.0, "TGD"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [esDomainMapper=" + esDomainMapper + ", fpDomainMapper=" + fpDomainMapper
                + ", pdfGenerator=" + pdfGenerator + "]";
    }

}
