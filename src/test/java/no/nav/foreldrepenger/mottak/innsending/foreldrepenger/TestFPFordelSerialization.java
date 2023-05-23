package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.termin;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.common.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.VEDLEGG1;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.VEDLEGG2;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.VEDLEGG3;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengesøknad;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.svp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.EttersendingsType;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.AktørIdTilFnrConverter;
import no.nav.foreldrepenger.common.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V1SvangerskapspengerDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3EngangsstønadDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.config.JacksonConfiguration;
import no.nav.foreldrepenger.mottak.innsending.mappers.DelegerendeDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.pdf.MappablePdfGenerator;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;
import no.nav.foreldrepenger.mottak.util.JacksonWrapper;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    JacksonConfiguration.class,
    JacksonWrapper.class,
    MetdataGenerator.class,
    KonvoluttGenerator.class,
    DelegerendeDomainMapper.class,
    V3ForeldrepengerDomainMapper.class,
    V3EngangsstønadDomainMapper.class,
    V1SvangerskapspengerDomainMapper.class
})
class TestFPFordelSerialization {
    private static final AktørId AKTØRID = new AktørId("1111111111");
    private static final List<EnkeltArbeidsforhold> ARB_FORHOLD = arbeidsforhold();

    @MockBean
    private AktørIdTilFnrConverter aktørIdTilFnrConverter;

    @MockBean
    @Qualifier(DELEGERENDE)
    private MappablePdfGenerator mappablePdfGenerator;
    @MockBean
    private ArbeidsforholdTjeneste arbeidsforhold;

    @Autowired
    private KonvoluttGenerator konvoluttGenerator;
    @Autowired
    @Qualifier(DELEGERENDE)
    private DomainMapper domainMapper;

    @BeforeEach
    void before() {
        when(arbeidsforhold.hentArbeidsforhold()).thenReturn(ARB_FORHOLD);
        when(mappablePdfGenerator.generer(any(), any(), any())).thenReturn(new byte[0]);
        when(aktørIdTilFnrConverter.konverter(any())).thenReturn(new AktørId("1234"));
    }

    @Test
    void testESXMLKonverteringOK() {
        var engangstønad = engangssøknad(false, termin(), VEDLEGG3);
        assertNotNull(domainMapper.tilXML(engangstønad, AKTØRID, SøknadEgenskap.of(INITIELL_ENGANGSSTØNAD)));

    }

    @Test
    void testSVPXMLKonverteringOK() {
        var svp = svp();
        assertNotNull(domainMapper.tilXML(svp, AKTØRID, SøknadEgenskap.of(INITIELL_SVANGERSKAPSPENGER)));
    }

    @Test
    void testFPXMLKonverteringOK() {
        var foreldrepengesøknad = foreldrepengesøknad();
        assertNotNull(domainMapper.tilXML(foreldrepengesøknad, AKTØRID, SøknadEgenskap.of(INITIELL_FORELDREPENGER)));
    }

    @Test
    void testFPEndringXMLKonverteringOK() {
        var endringssøknad = endringssøknad(VEDLEGG2);
        assertNotNull(domainMapper.tilXML(endringssøknad, AKTØRID, SøknadEgenskap.of(ENDRING_FORELDREPENGER)));
    }



    @Test
    void testKonvolutt() {
        var søknad = foreldrepengesøknad( false, valgfrittVedlegg(ForeldrepengerTestUtils.ID142, LASTET_OPP));
        var innsendingPersonInfo = new InnsendingPersonInfo(person().navn(), person().aktørId(), person().fnr());
        var konvolutt = konvoluttGenerator.generer(søknad, SøknadEgenskap.of(INITIELL_FORELDREPENGER), innsendingPersonInfo);
        assertNotNull(konvolutt.getMetadata());
        assertEquals(1, konvolutt.getVedlegg().size());
        assertEquals(søknad, konvolutt.getInnsending());
        assertNotNull(konvolutt.XMLHovedDokument());
        assertNotNull(konvolutt.PDFHovedDokument());
        assertTrue(konvolutt.erInitiellForeldrepenger());
    }

    @Test
    void testKonvoluttEndring() {
        var es = endringssøknad(ForeldrepengerTestUtils.VEDLEGG1, VEDLEGG2);
        var innsendingPersonInfo = new InnsendingPersonInfo(person().navn(), person().aktørId(), person().fnr());
        var konvolutt = konvoluttGenerator.generer(es, SøknadEgenskap.of(ENDRING_FORELDREPENGER), innsendingPersonInfo);
        assertNotNull(konvolutt.getMetadata());
        assertNotNull(konvolutt.XMLHovedDokument());
        assertNotNull(konvolutt.PDFHovedDokument());
        assertEquals(es, konvolutt.getInnsending());
        assertEquals(2, konvolutt.getVedlegg().size());
        assertTrue(konvolutt.erEndring());
    }

    @Test
    void testKonvoluttEttersending() {
        var es = new Ettersending(new Saksnummer("42"), EttersendingsType.FORELDREPENGER, List.of(VEDLEGG1, VEDLEGG2), null);
        var konvolutt = konvoluttGenerator.generer(es, SøknadEgenskap.ETTERSENDING_FORELDREPENGER, person().aktørId());
        assertNotNull(konvolutt.getMetadata());
        assertEquals(2, konvolutt.getVedlegg().size());
        assertNull(konvolutt.XMLHovedDokument());
        assertNull(konvolutt.PDFHovedDokument());
        assertEquals(es, konvolutt.getInnsending());
        assertTrue(konvolutt.erEttersending());

    }

    private static List<EnkeltArbeidsforhold> arbeidsforhold() {
        return List.of(EnkeltArbeidsforhold.builder()
                .arbeidsgiverId("1234")
                .from(LocalDate.now().minusDays(200))
                .to(Optional.of(LocalDate.now()))
                .stillingsprosent(ProsentAndel.valueOf(90))
                .arbeidsgiverNavn("El Bedrifto").build());
    }
}
