package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.alleSøknadVersjoner;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.termin;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter.MULTIPART_MIXED_VALUE;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.config.TestConfig;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.EttersendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Fordeling;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsending.mappers.DelegerendeDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
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
@AutoConfigureJsonTesters
@ContextConfiguration(classes = { MottakConfiguration.class, TestConfig.class })
public class TestFPFordelSerialization {

    private static final SøknadInspektør INSPEKTØR = new XMLStreamSøknadInspektør();

    @Mock
    private Oppslag oppslag;

    private FPFordelKonvoluttGenerator konvoluttGenerator;

    @Inject
    MottakConfiguration cfg;
    @Inject
    ObjectMapper objectMapper;

    private XMLSøknadMapper v123XMLMapper;
    private DomainMapper v23DomainMapper;

    private static final AktorId AKTØRID = new AktorId("1111111111");
    private static final Fødselsnummer FNR = new Fødselsnummer("01010111111");
    private static final List<Arbeidsforhold> ARB_FORHOLD = arbeidsforhold();

    @BeforeEach
    public void before() {
        v123XMLMapper = new DelegerendeXMLSøknadMapper(
                new V1ForeldrepengerXMLMapper(oppslag),
                new V2ForeldrepengerXMLMapper(oppslag),
                new V3ForeldrepengerXMLMapper(oppslag));
        v23DomainMapper = new DelegerendeDomainMapper(
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
        alleSøknadVersjoner().forEach(this::testEndringssøknadRoundtrip);
    }

    @Test
    public void testESFpFordel() {
        Søknad engangstønad = engangssøknad(DEFAULT_VERSJON, false, termin(), norskForelder(DEFAULT_VERSJON),
                ForeldrepengerTestUtils.V3);
        String xml = v23DomainMapper.tilXML(engangstønad, AKTØRID,
                new SøknadEgenskap(SøknadType.INITIELL_ENGANGSSTØNAD));
        System.out.println(xml);
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
        Ettersending es = new Ettersending(EttersendingsType.foreldrepenger, "42", ForeldrepengerTestUtils.VEDLEGG1,
                ForeldrepengerTestUtils.V2);
        FPFordelKonvolutt<Ettersending> konvolutt = konvoluttGenerator.payload(es, person());
        List<HttpEntity<?>> metadata = konvolutt.getMetadata();
        assertEquals(1, metadata.size());
        List<HttpEntity<?>> vedlegg = konvolutt.getVedlegg();
        assertEquals(2, vedlegg.size());
        assertEquals(null, konvolutt.getHoveddokumenter());
        assertMediaType(vedlegg.get(1), APPLICATION_PDF_VALUE);
        assertMediaType(vedlegg.get(0), APPLICATION_PDF_VALUE);
    }

    private void testSøknadRoundtrip(Versjon v) {
        Søknad original = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(v);
        String xml = v23DomainMapper.tilXML(original, AKTØRID,
                new SøknadEgenskap(v, SøknadType.INITIELL_FORELDREPENGER));
        SøknadEgenskap inspiser = INSPEKTØR.inspiser(xml);
        assertEquals(inspiser.getVersjon(), v);
        assertEquals(inspiser.getType(), SøknadType.INITIELL_FORELDREPENGER);
        Søknad respons = v123XMLMapper.tilSøknad(xml, inspiser);
        assertEquals(original, respons);
    }

    public void testEndringssøknadRoundtrip(Versjon v) {
        Endringssøknad original = endringssøknad(v, ForeldrepengerTestUtils.VEDLEGG1, ForeldrepengerTestUtils.V2);
        String xml = v23DomainMapper.tilXML(original, AKTØRID,
                new SøknadEgenskap(v, SøknadType.ENDRING_FORELDREPENGER));
        SøknadEgenskap inspiser = INSPEKTØR.inspiser(xml);
        assertEquals(inspiser.getVersjon(), v);
        assertEquals(inspiser.getType(), SøknadType.ENDRING_FORELDREPENGER);
        Endringssøknad respons = Endringssøknad.class.cast(v123XMLMapper.tilSøknad(xml, inspiser));
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
        FPFordelKonvolutt<Søknad> konvolutt = konvoluttGenerator.payload(søknad, person(),
                new SøknadEgenskap(v, SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(3, konvolutt.antallElementer());
        List<HttpEntity<?>> metadata = konvolutt.getMetadata();
        List<HttpEntity<?>> hoveddokumenter = konvolutt.getHoveddokumenter();
        List<HttpEntity<?>> vedlegg = konvolutt.getVedlegg();
        assertEquals(1, metadata.size());
        assertEquals(2, hoveddokumenter.size());
        assertEquals(1, vedlegg.size());
        assertMediaType(konvolutt.getPayload(), MULTIPART_MIXED_VALUE);
        assertMediaType(metadata.get(0), APPLICATION_JSON_UTF8_VALUE);
        assertMediaType(hoveddokumenter.get(0), APPLICATION_XML_VALUE);
        assertNotNull(konvolutt.getXMLDokument());
        assertMediaType(hoveddokumenter.get(1), APPLICATION_PDF_VALUE);
        assertMediaType(vedlegg.get(0), APPLICATION_PDF_VALUE);
    }

    private void testKonvoluttEndring(Versjon v) {
        Endringssøknad es = endringssøknad(v, ForeldrepengerTestUtils.VEDLEGG1, ForeldrepengerTestUtils.V2);

        FPFordelKonvolutt<Endringssøknad> konvolutt = konvoluttGenerator.payload(es, person(),
                new SøknadEgenskap(v, SøknadType.ENDRING_FORELDREPENGER));
        List<HttpEntity<?>> metadata = konvolutt.getMetadata();
        List<HttpEntity<?>> vedlegg = konvolutt.getVedlegg();
        assertEquals(1, metadata.size());
        assertEquals(2, vedlegg.size());
        assertMediaType(vedlegg.get(1), APPLICATION_PDF_VALUE);
        assertMediaType(vedlegg.get(0), APPLICATION_PDF_VALUE);
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
                v23DomainMapper, pdf);
    }
}
