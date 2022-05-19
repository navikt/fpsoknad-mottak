package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.LOCAL;
import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.TEST;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.VEDLEGG1;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengeSøknad;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengesøknadMedEttIkkeOpplastedVedlegg;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.svp;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.config.TestConfig;
import no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGeneratorStub;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;
import no.nav.foreldrepenger.mottak.util.TokenUtil;
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
        NySvangerskapspengerPdfGenerator.class,
        DelegerendePDFGenerator.class,
        InfoskrivRenderer.class,
        InfoskrivPdfEkstraktor.class,
        SvangerskapspengerInfoRenderer.class,
        SpringTokenValidationContextHolder.class, TestConfig.class })
class MappablePdfGeneratorTest {

    private static final String TILLEGGSOPPLYSNINGER = "Begrunnelse for å søke om utsettelse, " +
            "på grunn av sykdom tilbake i tid: Jeg var innlagt på sykehus og hadde ingen " +
            "mulighet til å søke om utsettelse.";
    private static final List<EnkeltArbeidsforhold> ARB_FORHOLD = arbeidsforhold();

    @Inject
    @Qualifier(DELEGERENDE)
    MappablePdfGenerator gen;

    @Inject
    InfoskrivPdfEkstraktor pdfExtracter;

    @MockBean
    ArbeidsforholdTjeneste arbeidsforholdTjeneste;

    @MockBean
    TokenUtil tokenUtil;

    @BeforeEach
    void before() {
        when(arbeidsforholdTjeneste.hentArbeidsforhold()).thenReturn(ARB_FORHOLD);
    }

    @Test
    void signature() {
        assertTrue(hasPdfSignature(
                gen.generer(foreldrepengeSøknad(), person(), INITIELL_FORELDREPENGER)));
    }

    @Test
    void førstegangssøknad() throws Exception {
        try (FileOutputStream fos = new FileOutputStream("søknad.pdf")) {
            Søknad søknad = foreldrepengesøknadMedEttIkkeOpplastedVedlegg(true);
            søknad.setTilleggsopplysninger(TILLEGGSOPPLYSNINGER);
            fos.write(gen.generer(søknad, person(), INITIELL_FORELDREPENGER));
        }
    }

    @Test
    void foreldrepengerFortsettUtenArbeidsforholdVedExceptionFraTjeneste() throws Exception {
        when(arbeidsforholdTjeneste.hentArbeidsforhold()).thenThrow(RuntimeException.class);
        try (FileOutputStream fos = new FileOutputStream("søknad_exception_fra_arbeidsforholdtjeneste.pdf")) {
            Søknad søknad = foreldrepengesøknadMedEttIkkeOpplastedVedlegg(true);
            søknad.setTilleggsopplysninger(TILLEGGSOPPLYSNINGER);
            fos.write(gen.generer(søknad, person(), INITIELL_FORELDREPENGER));
        }
    }

    @Test
    void endring() throws Exception {
        try (FileOutputStream fos = new FileOutputStream("endring.pdf")) {
            Endringssøknad endringssøknad = endringssøknad(VEDLEGG1);
            endringssøknad.setTilleggsopplysninger(TILLEGGSOPPLYSNINGER);
            fos.write(gen.generer(endringssøknad, person(), ENDRING_FORELDREPENGER));
        }
    }

    @Test
    void engangs() throws Exception {
        when(tokenUtil.autentisertBruker()).thenReturn(new Fødselsnummer("010101010101"));
        try (var fos = new FileOutputStream("engangssøknad.pdf")) {
            fos.write(gen.generer(engangssøknad(fødsel(), true), person(), INITIELL_ENGANGSSTØNAD));
        }
    }

    @Test
    void svanger() throws Exception {
        try (var fos = new FileOutputStream("svangerskapspenger.pdf")) {
            fos.write(gen.generer(svp(), person(), INITIELL_SVANGERSKAPSPENGER));
        }
    }

    @Test
    void infoskrivSplitter() throws Exception {
        try (var fos = new FileOutputStream("infoskriv.pdf")) {
            Søknad søknad = foreldrepengesøknadMedEttIkkeOpplastedVedlegg(true);
            søknad.setTilleggsopplysninger(TILLEGGSOPPLYSNINGER);
            byte[] fullSøknadPdf = gen.generer(søknad, person(), INITIELL_FORELDREPENGER);
            byte[] infoskriv = pdfExtracter.infoskriv(fullSøknadPdf);
            if (infoskriv != null) {
                fos.write(infoskriv);
                assertTrue(hasPdfSignature(infoskriv));
            }
        }
    }

    private static List<EnkeltArbeidsforhold> arbeidsforhold() {
        return List.of(EnkeltArbeidsforhold.builder()
                .arbeidsgiverId("999263550")
                .from(LocalDate.now().minusDays(200))
                .to(Optional.of(LocalDate.now().plusDays(10)))
                .stillingsprosent(ProsentAndel.valueOf(90))
                .arbeidsgiverNavn("Den Første Bedriften").build());
    }
}
