package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.termin;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter.MULTIPART_MIXED_VALUE;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.HOVEDDOKUMENT;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.METADATA;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.VEDLEGG;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;
import static no.nav.foreldrepenger.mottak.util.Versjon.alleSøknadVersjoner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.config.TestConfig;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.EttersendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.errorhandling.VersionMismatchException;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsending.mappers.DelegerendeDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.V1ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.V2EngangsstønadDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.V2ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.V3EngangsstønadDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.pdf.DelegerendePDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.EngangsstønadPDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengeInfoRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengerPDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.PDFElementRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.SøknadTextFormatter;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.SøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.mappers.DelegerendeXMLSøknadMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.V1ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.V2ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.V3ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.XMLSøknadMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.Versjon;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@MockitoSettings(strictness = LENIENT)
@ContextConfiguration(classes = { MottakConfiguration.class, ObjectMapper.class, TestConfig.class })
public class TestFPFordelSerialization {

    private static final SøknadInspektør INSPEKTØR = new XMLStreamSøknadInspektør();

    @Mock
    private Oppslag oppslag;

    private FPFordelKonvoluttGenerator konvoluttGenerator;

    @Inject
    MottakConfiguration cfg;
    @Inject
    ObjectMapper objectMapper;

    private XMLSøknadMapper v12XMLMapper;
    private DomainMapper v12DomainMapper;

    private static final AktorId AKTØRID = new AktorId("1111111111");
    private static final Fødselsnummer FNR = new Fødselsnummer("01010111111");
    private static final List<Arbeidsforhold> ARB_FORHOLD = arbeidsforhold();

    @BeforeEach
    public void before() {
        v12XMLMapper = new DelegerendeXMLSøknadMapper(
                new V1ForeldrepengerXMLMapper(oppslag),
                new V2ForeldrepengerXMLMapper(oppslag),
                new V3ForeldrepengerXMLMapper(oppslag));
        v12DomainMapper = new DelegerendeDomainMapper(
                new V1ForeldrepengerDomainMapper(oppslag),
                new V2ForeldrepengerDomainMapper(oppslag),
                new V3ForeldrepengerDomainMapper(oppslag),
                new V2EngangsstønadDomainMapper(oppslag),
                new V3EngangsstønadDomainMapper(oppslag));
        konvoluttGenerator = konvoluttGenerator();
        when(oppslag.getAktørId(eq(FNR))).thenReturn(AKTØRID);
        when(oppslag.getFnr(eq(AKTØRID))).thenReturn(FNR);
        when(oppslag.getArbeidsforhold()).thenReturn(ARB_FORHOLD);
    }

    @Test
    public void testEndringssøknadRoundtrip() {
        alleSøknadVersjoner().stream()
                .forEach(v -> testEndringssøknadRoundtrip(v));
    }

    @Test
    public void testESFpFordel() {
        Søknad engangstønad = engangssøknad(V2, false, termin(), norskForelder(V2), ForeldrepengerTestUtils.V3);
        String xml = v12DomainMapper.tilXML(engangstønad, AKTØRID,
                new SøknadEgenskap(SøknadType.INITIELL_ENGANGSSTØNAD));
        System.out.println(xml);
    }

    @Test
    public void testSøknadRoundtrip() {
        alleSøknadVersjoner().stream()
                .forEach(v -> testSøknadRoundtrip(v));
    }

    @Test
    public void testFeilMapper() throws Exception {
        assertThrows(VersionMismatchException.class,
                () -> v12DomainMapper.tilXML(søknadMedEttOpplastetEttIkkeOpplastetVedlegg(V1), AKTØRID,
                        new SøknadEgenskap(V2, SøknadType.ENDRING_FORELDREPENGER)));
    }

    @Test
    public void testKonvolutt() {
        alleSøknadVersjoner()
                .stream()
                .forEach(v -> testKonvolutt(v));
    }

    @Test
    public void testKonvoluttEndring() {
        alleSøknadVersjoner()
                .stream()
                .forEach(v -> testKonvoluttEndring(v));
    }

    @Test
    public void testKonvoluttEttersending() {
        Ettersending es = new Ettersending(EttersendingsType.foreldrepenger, "42", ForeldrepengerTestUtils.VEDLEGG1,
                ForeldrepengerTestUtils.V2);
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> konvolutt = konvoluttGenerator.payload(es, person());
        List<HttpEntity<?>> metadata = konvolutt.getBody().get(METADATA);
        assertEquals(1, metadata.size());
        List<HttpEntity<?>> vedlegg = konvolutt.getBody().get(VEDLEGG);
        assertEquals(2, vedlegg.size());
        assertEquals(null, konvolutt.getBody().get(HOVEDDOKUMENT));
        assertMediaType(vedlegg.get(1), APPLICATION_PDF_VALUE);
        assertMediaType(vedlegg.get(0), APPLICATION_PDF_VALUE);
    }

    private void testSøknadRoundtrip(Versjon v) {
        Søknad original = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(v);
        String xml = v12DomainMapper.tilXML(original, AKTØRID,
                new SøknadEgenskap(v, SøknadType.INITIELL_FORELDREPENGER));
        SøknadEgenskap inspiser = INSPEKTØR.inspiser(xml);
        assertEquals(inspiser.getVersjon(), v);
        assertEquals(inspiser.getType(), SøknadType.INITIELL_FORELDREPENGER);
        Søknad respons = v12XMLMapper.tilSøknad(xml, inspiser);
        assertEquals(original, respons);
    }

    public void testEndringssøknadRoundtrip(Versjon v) {
        Endringssøknad original = endringssøknad(v, ForeldrepengerTestUtils.VEDLEGG1, ForeldrepengerTestUtils.V2);
        String xml = v12DomainMapper.tilXML(original, AKTØRID,
                new SøknadEgenskap(v, SøknadType.ENDRING_FORELDREPENGER));
        SøknadEgenskap inspiser = INSPEKTØR.inspiser(xml);
        assertEquals(inspiser.getVersjon(), v);
        assertEquals(inspiser.getType(), SøknadType.ENDRING_FORELDREPENGER);
        Endringssøknad respons = Endringssøknad.class.cast(v12XMLMapper.tilSøknad(xml, inspiser));
        Fordeling originalFordeling = no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.class
                .cast(original.getYtelse()).getFordeling();
        Fordeling responsFordeling = no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.class
                .cast(respons.getYtelse()).getFordeling();
        assertEquals(originalFordeling, responsFordeling);
        assertEquals(original.getSaksnr(), respons.getSaksnr());
    }

    private void testKonvolutt(Versjon v) {
        Søknad søknad = søknad(v, false,
                valgfrittVedlegg(ForeldrepengerTestUtils.ID142, InnsendingsType.LASTET_OPP));
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> konvolutt = konvoluttGenerator.payload(søknad, person(),
                new SøknadEgenskap(v, SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(3, konvolutt.getBody().size());
        List<HttpEntity<?>> metadata = konvolutt.getBody().get(METADATA);
        List<HttpEntity<?>> hoveddokumenter = konvolutt.getBody().get(HOVEDDOKUMENT);
        List<HttpEntity<?>> vedlegg = konvolutt.getBody().get(VEDLEGG);
        assertEquals(1, metadata.size());
        assertEquals(2, hoveddokumenter.size());
        assertEquals(1, vedlegg.size());
        assertMediaType(konvolutt, MULTIPART_MIXED_VALUE);
        assertMediaType(metadata.get(0), APPLICATION_JSON_UTF8_VALUE);
        assertMediaType(hoveddokumenter.get(0), APPLICATION_XML_VALUE);
        assertMediaType(hoveddokumenter.get(1), APPLICATION_PDF_VALUE);
        assertMediaType(vedlegg.get(0), APPLICATION_PDF_VALUE);
    }

    private void testKonvoluttEndring(Versjon v) {
        Endringssøknad es = endringssøknad(v, ForeldrepengerTestUtils.VEDLEGG1, ForeldrepengerTestUtils.V2);

        HttpEntity<MultiValueMap<String, HttpEntity<?>>> konvolutt = konvoluttGenerator.payload(es, person(),
                new SøknadEgenskap(v, SøknadType.ENDRING_FORELDREPENGER));
        List<HttpEntity<?>> metadata = konvolutt.getBody().get(METADATA);
        List<HttpEntity<?>> vedlegg = konvolutt.getBody().get(VEDLEGG);
        assertEquals(1, metadata.size());
        assertEquals(2, vedlegg.size());
        assertMediaType(vedlegg.get(1), APPLICATION_PDF_VALUE);
        assertMediaType(vedlegg.get(0), APPLICATION_PDF_VALUE);
    }

    private static ValgfrittVedlegg opplastetVedlegg(String id, DokumentType type) {
        try {
            return new ValgfrittVedlegg(id, InnsendingsType.LASTET_OPP, type,
                    new ClassPathResource("terminbekreftelse.pdf"));
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private static List<Arbeidsforhold> arbeidsforhold() {
        return newArrayList(
                new Arbeidsforhold("1234", "", LocalDate.now().minusDays(200),
                        Optional.of(LocalDate.now()), 90.0, "El Bedrifto"));
    }

    private static void assertMediaType(HttpEntity<?> entity, String type) {
        assertEquals(entity.getHeaders().get(CONTENT_TYPE).get(0), type);
    }

    private FPFordelKonvoluttGenerator konvoluttGenerator() {
        PDFElementRenderer renderer = new PDFElementRenderer();
        SøknadTextFormatter textFormatter = new SøknadTextFormatter(cfg.landkoder(), cfg.kvitteringstekster());
        ForeldrepengeInfoRenderer fpRenderer = new ForeldrepengeInfoRenderer(renderer,
                textFormatter);
        ForeldrepengerPDFGenerator fp = new ForeldrepengerPDFGenerator(oppslag, fpRenderer);
        EngangsstønadPDFGenerator es = new EngangsstønadPDFGenerator(textFormatter, renderer);
        DelegerendePDFGenerator pdf = new DelegerendePDFGenerator(fp, es);
        return new FPFordelKonvoluttGenerator(new FPFordelMetdataGenerator(objectMapper),
                v12DomainMapper, pdf);
    }
}
