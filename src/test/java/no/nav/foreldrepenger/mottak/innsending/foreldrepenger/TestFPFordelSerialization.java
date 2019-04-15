package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.mottak.domain.felles.EttersendingsType.foreldrepenger;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.alleSøknadVersjoner;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.termin;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.V2;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.V3;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.VEDLEGG1;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter.MULTIPART_MIXED_VALUE;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Fordeling;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.mappers.XMLSøknadMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.Versjon;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@MockitoSettings(strictness = LENIENT)
@AutoConfigureJsonTesters
@ComponentScan(basePackages = "no.nav.foreldrepenger.mottak")
public class TestFPFordelSerialization {

    private static final Inspektør INSPEKTØR = new XMLStreamSøknadInspektør();

    @MockBean
    private Oppslag oppslag;

    @Inject
    private FPFordelKonvoluttGenerator konvoluttGenerator;

    @Inject
    @Qualifier(DELEGERENDE)
    private XMLSøknadMapper xmlMapper;
    @Inject
    @Qualifier(DELEGERENDE)
    private DomainMapper domainMapper;

    private static final AktørId AKTØRID = new AktørId("1111111111");
    private static final Fødselsnummer FNR = new Fødselsnummer("01010111111");
    private static final List<Arbeidsforhold> ARB_FORHOLD = arbeidsforhold();

    @BeforeEach
    public void before() {
        when(oppslag.getAktørId(eq(FNR))).thenReturn(AKTØRID);
        when(oppslag.getFnr(eq(AKTØRID))).thenReturn(FNR);
        when(oppslag.getArbeidsforhold()).thenReturn(ARB_FORHOLD);
    }

    @Test
    public void testEndringssøknadRoundtrip() {
        alleSøknadVersjoner().forEach(this::testEndringssøknadRoundtrip);
    }

    @Test
    public void testESFpFordel() {
        Søknad engangstønad = engangssøknad(DEFAULT_VERSJON, false, termin(), norskForelder(DEFAULT_VERSJON), V3);
        assertNotNull(domainMapper.tilXML(engangstønad, AKTØRID, new SøknadEgenskap(INITIELL_ENGANGSSTØNAD)));
    }

    @Test
    public void testSøknadRoundtrip() {
        alleSøknadVersjoner().stream().forEach(v -> testSøknadRoundtrip(v));
    }

    @Test
    public void testKonvolutt() {
        alleSøknadVersjoner().forEach(this::testKonvolutt);
    }

    @Test
    public void testKonvoluttEndring() {
        alleSøknadVersjoner().forEach(this::testKonvoluttEndring);
    }

    @Test
    public void testKonvoluttEttersending() {
        Ettersending es = new Ettersending(foreldrepenger, "42", VEDLEGG1, V2);
        FPFordelKonvolutt konvolutt = konvoluttGenerator.generer(es, person());
        assertNotNull(konvolutt.getMetadata());
        assertEquals(2, konvolutt.getVedlegg().size());
        assertNull(konvolutt.XMLHovedDokument());
        assertNull(konvolutt.PDFHovedDokument());
    }

    private void testSøknadRoundtrip(Versjon v) {
        Søknad original = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(v);
        String xml = domainMapper.tilXML(original, AKTØRID, new SøknadEgenskap(v, INITIELL_FORELDREPENGER));
        SøknadEgenskap egenskap = INSPEKTØR.inspiser(xml);
        assertEquals(v, egenskap.getVersjon());
        assertEquals(egenskap.getType(), INITIELL_FORELDREPENGER);
        assertEquals(original, xmlMapper.tilSøknad(xml, egenskap));
    }

    public void testEndringssøknadRoundtrip(Versjon v) {
        Endringssøknad original = endringssøknad(v, VEDLEGG1, V2);
        String xml = domainMapper.tilXML(original, AKTØRID, new SøknadEgenskap(v, ENDRING_FORELDREPENGER));
        SøknadEgenskap egenskap = INSPEKTØR.inspiser(xml);
        assertEquals(v, egenskap.getVersjon());
        assertEquals(ENDRING_FORELDREPENGER, egenskap.getType());
        Endringssøknad respons = Endringssøknad.class.cast(xmlMapper.tilSøknad(xml, egenskap));
        Fordeling originalFordeling = no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.class
                .cast(original.getYtelse()).getFordeling();
        assertEquals(originalFordeling, no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.class
                .cast(respons.getYtelse()).getFordeling());
        assertEquals(original.getSaksnr(), respons.getSaksnr());
    }

    private void testKonvolutt(Versjon v) {
        Søknad søknad = søknad(v, false, valgfrittVedlegg(ForeldrepengerTestUtils.ID142, LASTET_OPP));
        FPFordelKonvolutt konvolutt = konvoluttGenerator.generer(søknad, person(),
                new SøknadEgenskap(v, INITIELL_FORELDREPENGER));
        assertNotNull(konvolutt.getMetadata());
        assertEquals(1, konvolutt.getVedlegg().size());
        assertMediaType(konvolutt.getPayload(), MULTIPART_MIXED_VALUE);
        assertNotNull(konvolutt.XMLHovedDokument());
        assertNotNull(konvolutt.PDFHovedDokument());
    }

    private void testKonvoluttEndring(Versjon v) {
        Endringssøknad es = endringssøknad(v, ForeldrepengerTestUtils.VEDLEGG1, ForeldrepengerTestUtils.V2);
        FPFordelKonvolutt konvolutt = konvoluttGenerator.generer(es, person(),
                new SøknadEgenskap(v, ENDRING_FORELDREPENGER));
        assertNotNull(konvolutt.getMetadata());
        assertNotNull(konvolutt.XMLHovedDokument());
        assertNotNull(konvolutt.PDFHovedDokument());
        assertEquals(2, konvolutt.getVedlegg().size());
    }

    private static List<Arbeidsforhold> arbeidsforhold() {
        return newArrayList(
                new Arbeidsforhold("1234", "", LocalDate.now().minusDays(200),
                        Optional.of(LocalDate.now()), new ProsentAndel(90), "El Bedrifto"));
    }

    private static void assertMediaType(HttpEntity<?> entity, String type) {
        assertEquals(type, entity.getHeaders().getFirst(CONTENT_TYPE));
    }
}
