package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.boot.conditionals.EnvUtil.LOCAL;
import static no.nav.boot.conditionals.EnvUtil.TEST;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.common.innsending.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadEgenskap.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsending.SøknadEgenskap.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadEgenskap.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.NORSK_FORELDER_FNR;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.VEDLEGG1;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.fordeling;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengesøknad;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengesøknadMedEttIkkeOpplastedVedlegg;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.norskForelder;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.rettigheter;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.svp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.TestUtils;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;
import no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGeneratorStub;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder;

@AutoConfigureJsonTesters
@ActiveProfiles(profiles = { LOCAL, TEST })
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        MottakConfiguration.class,
        SøknadTextFormatter.class,
        ForeldrepengeInfoRenderer.class,
        PdfElementRenderer.class,
        ForeldrepengerPdfGenerator.class,
        EngangsstønadPdfGenerator.class,
        RestTemplate.class,
        PdfGeneratorStub.class,
        DelegerendePDFGenerator.class,
        InfoskrivRenderer.class,
        InfoskrivPdfEkstraktor.class,
        SvangerskapspengerInfoRenderer.class,
        SvangerskapspengerPdfGenerator.class,
        SpringTokenValidationContextHolder.class })
class MappablePdfGeneratorTest {

    private static final String TILLEGGSOPPLYSNINGER = "Begrunnelse for å søke om utsettelse, " +
            "på grunn av sykdom tilbake i tid: Jeg var innlagt på sykehus og hadde ingen " +
            "mulighet til å søke om utsettelse.";
    private static final List<EnkeltArbeidsforhold> ARB_FORHOLD = arbeidsforhold();

    @Autowired
    @Qualifier(DELEGERENDE)
    MappablePdfGenerator gen;

    @Autowired
    InfoskrivPdfEkstraktor pdfExtracter;

    @MockBean
    ArbeidsforholdTjeneste arbeidsforholdTjeneste;

    @MockBean
    TokenUtil tokenUtil;

    private String ABSOLUTE_PATH;

    @BeforeEach
    void before() {
        when(arbeidsforholdTjeneste.hentArbeidsforhold()).thenReturn(ARB_FORHOLD);
        var classLoader = getClass().getClassLoader();
        var file = new File(classLoader.getResource(".").getFile());
        ABSOLUTE_PATH = file.getAbsolutePath();
    }

    @Test
    void signature() {
        assertTrue(hasPdfSignature(gen.generer(foreldrepengesøknad(), INITIELL_FORELDREPENGER, personInfo())));
    }

    private static InnsendingPersonInfo personInfo() {
        return new InnsendingPersonInfo(person().navn(), person().aktørId(), person().fnr());
    }

    @Test
    void førstegangssøknad() throws Exception {
        var filNavn = ABSOLUTE_PATH +"/søknad.pdf";
        var søknad = foreldrepengesøknadMedEttIkkeOpplastedVedlegg(true);
        try (var fos = new FileOutputStream(filNavn)) {
            assertDoesNotThrow(() -> fos.write(gen.generer(søknad, INITIELL_FORELDREPENGER, personInfo())));
        }

        assertThat(søknad.getTilleggsopplysninger()).isNotNull();
        verifiserGenerertPDF(filNavn, 6, søknad.getTilleggsopplysninger());
    }

    @Test
    void foreldrepengerFortsettUtenArbeidsforholdVedExceptionFraTjeneste() throws Exception {
        var filNavn = ABSOLUTE_PATH +"/søknad_exception_fra_arbeidsforholdtjeneste.pdf";
        when(arbeidsforholdTjeneste.hentArbeidsforhold()).thenThrow(RuntimeException.class);
        var søknad = foreldrepengesøknadMedEttIkkeOpplastedVedlegg(true);
        try (var fos = new FileOutputStream(filNavn)) {
            assertDoesNotThrow(() -> fos.write(gen.generer(søknad, INITIELL_FORELDREPENGER, personInfo())));
        }

        assertThat(søknad.getTilleggsopplysninger()).isNotNull();
        verifiserGenerertPDF(filNavn, 4, søknad.getTilleggsopplysninger());
    }

    @Test
    void endring() throws Exception {
        var filNavn = ABSOLUTE_PATH +"/endring.pdf";
        var endringssøknad = new Endringssøknad(
            LocalDate.now(),
            TestUtils.søker(),
            new Foreldrepenger(norskForelder(), fødsel(), rettigheter(), null, null,
                fordeling(VEDLEGG1.getMetadata().id()), null),
            TILLEGGSOPPLYSNINGER,
            List.of(VEDLEGG1),
            new Saksnummer("123456789"));
        try (var fos = new FileOutputStream(filNavn)) {
            assertDoesNotThrow(() -> fos.write(gen.generer(endringssøknad, ENDRING_FORELDREPENGER, personInfo())));
        }

        verifiserGenerertPDF(filNavn, 3, TILLEGGSOPPLYSNINGER);
    }

    @Test
    void engangs() throws Exception {
        when(tokenUtil.autentisertBrukerOrElseThrowException()).thenReturn(new Fødselsnummer("010101010101"));
        var filNavn = ABSOLUTE_PATH + "/engangssøknad.pdf";
        try (var fos = new FileOutputStream(filNavn)) {
            fos.write(gen.generer(engangssøknad(false), INITIELL_ENGANGSSTØNAD, personInfo()));
        }
        //verifiserGenerertPDF(filNavn, 1, "Søknad om engangsstønad");
    }

    @Test
    void svanger() throws Exception {
        var filNavn = ABSOLUTE_PATH + "/svangerskapspenger.pdf";
        try (var fos = new FileOutputStream(filNavn)) {
            fos.write(gen.generer(svp(), INITIELL_SVANGERSKAPSPENGER, personInfo()));
        }
        verifiserGenerertPDF(filNavn, 4, "Søknad om svangerskapspenger");
    }

    @Test
    void infoskrivSplitter() throws Exception {
        var filNavn = ABSOLUTE_PATH +"/infoskriv.pdf";
        try (var fos = new FileOutputStream(filNavn)) {
            var søknad = foreldrepengesøknadMedEttIkkeOpplastedVedlegg(true);
            var fullSøknadPdf = gen.generer(søknad, INITIELL_FORELDREPENGER, personInfo());
            var infoskriv = pdfExtracter.infoskriv(fullSøknadPdf);
            if (infoskriv != null) {
                fos.write(infoskriv);
                assertTrue(hasPdfSignature(infoskriv));
            }
        }
        verifiserGenerertPDF(filNavn, 1, "NAV trenger inntektsmelding så snart som mulig");
    }

    private void verifiserGenerertPDF(String filNavn, int antallSiderIPDFen, String forventetTekst) throws IOException {
        try (var document = Loader.loadPDF(new File(filNavn))) {
            assertThat(document.getNumberOfPages()).isEqualTo(antallSiderIPDFen);
            assertThat(document.isEncrypted()).isFalse();

            var pdfStripper = new PDFTextStripper();
            var text = pdfStripper.getText(document);
            assertThat(text).containsIgnoringWhitespaces(forventetTekst);
        }
    }

    private static List<EnkeltArbeidsforhold> arbeidsforhold() {
        return List.of(EnkeltArbeidsforhold.builder()
                .arbeidsgiverId("342352362")
                .from(LocalDate.now().minusDays(200))
                .to(Optional.empty())
                .stillingsprosent(ProsentAndel.valueOf(90))
                .arbeidsgiverNavn("Den Første Bedriften").build(),
            EnkeltArbeidsforhold.builder()
                .arbeidsgiverId(NORSK_FORELDER_FNR.value())
                .from(LocalDate.now().minusYears(10))
                .stillingsprosent(ProsentAndel.valueOf(10))
                .to(Optional.empty())
                .arbeidsgiverNavn("Test Arbeidsgiversen").build());
    }
}
