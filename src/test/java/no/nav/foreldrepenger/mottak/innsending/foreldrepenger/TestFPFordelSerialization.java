package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I500002;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I500005;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ID142;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ID143;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter.MULTIPART_MIXED_VALUE;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.HOVEDDOKUMENT;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.METADATA;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.VEDLEGG;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;
import static no.nav.foreldrepenger.mottak.util.Versjon.alleVersjoner;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.config.TestConfig;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.http.errorhandling.VersionMismatchException;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengeInfoRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengerPDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.PDFElementRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.SøknadTextFormatter;
import no.nav.foreldrepenger.mottak.innsyn.DelegerendeXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.V1XMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.V2XMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.Versjon;

@RunWith(SpringRunner.class)
@AutoConfigureJsonTesters
@ContextConfiguration(classes = { MottakConfiguration.class, SøknadTextFormatter.class, ForeldrepengeInfoRenderer.class,
        PDFElementRenderer.class,
        ForeldrepengerPDFGenerator.class, TestConfig.class })
public class TestFPFordelSerialization {

    private static final SøknadInspektør INSPEKTØR = new XMLStreamSøknadInspektør();

    @Mock
    private Oppslag oppslag;

    private FPFordelKonvoluttGenerator konvoluttGenerator;

    @Inject
    PDFElementRenderer pdfRenderer;
    @Inject
    ForeldrepengeInfoRenderer fpRenderer;
    @Inject
    ObjectMapper objectMapper;

    private DelegerendeXMLMapper v12XMLMapper;
    private VersjonsBevisstDomainMapper v12DomainMapper;

    private static final ValgfrittVedlegg VEDLEGG1 = opplastetVedlegg(ID142, I500002);
    private static final ValgfrittVedlegg VEDLEGG2 = opplastetVedlegg(ID143, I500005);

    private static final AktorId AKTØRID = new AktorId("1111111111");
    private static final Fødselsnummer FNR = new Fødselsnummer("01010111111");
    private static final List<Arbeidsforhold> ARB_FORHOLD = arbeidsforhold();

    @Before
    public void before() {
        v12XMLMapper = new DelegerendeXMLMapper(
                new V1XMLMapper(oppslag),
                new V2XMLMapper(oppslag));
        v12DomainMapper = new DelegerendeDomainMapper(
                new V2DomainMapper(oppslag),
                new V1DomainMapper(oppslag));
        konvoluttGenerator = konvoluttGenerator();
        when(oppslag.getAktørId(eq(FNR))).thenReturn(AKTØRID);
        when(oppslag.getFnr(eq(AKTØRID))).thenReturn(FNR);
        when(oppslag.getArbeidsforhold()).thenReturn(ARB_FORHOLD);
    }

    @Test
    public void testEndringssøknadRoundtrip() {
        alleVersjoner().stream()
                .forEach(v -> testEndringssøknadRoundtrip(v));
    }

    @Test
    public void testSøknadRoundtrip() {
        alleVersjoner().stream()
                .forEach(v -> testSøknadRoundtrip(v));
    }

    @Test(expected = VersionMismatchException.class)
    public void testFeilMapper() throws Exception {
        v12DomainMapper.tilXML(søknadMedEttOpplastetEttIkkeOpplastetVedlegg(V1), AKTØRID, V2);
    }

    @Test
    public void testInspektør() throws Exception {
        SøknadInspektør inspektør = new XMLStreamSøknadInspektør();
        String xml = StreamUtils.copyToString(new ClassPathResource("v1response.xml").getInputStream(),
                Charset.defaultCharset());
        SøknadEgenskaper egenskaper = inspektør.inspiser(xml);
        System.out.println(egenskaper);
    }

    @Test
    public void testKonvolutt() {
        alleVersjoner().stream()
                .forEach(v -> testKonvolutt(v));
    }

    @Test
    public void testKonvoluttEndring() {
        alleVersjoner().stream()
                .forEach(v -> testKonvoluttEndring(v));
    }

    @Test
    public void testKonvoluttEttersending() {
        Ettersending es = new Ettersending("42", VEDLEGG1, VEDLEGG2);
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
        String xml = v12DomainMapper.tilXML(original, AKTØRID, v);
        System.out.println(xml);
        SøknadEgenskaper inspiser = INSPEKTØR.inspiser(xml);
        assertEquals(inspiser.getVersjon(), v);
        assertEquals(inspiser.getType(), SøknadType.INITIELL);
        Søknad respons = v12XMLMapper.tilSøknad(xml);
        assertEquals(original, respons);
    }

    public void testEndringssøknadRoundtrip(Versjon v) {
        Endringssøknad original = endringssøknad(v, VEDLEGG1, VEDLEGG2);
        String xml = v12DomainMapper.tilXML(original, AKTØRID, v);
        SøknadEgenskaper inspiser = INSPEKTØR.inspiser(xml);
        assertEquals(inspiser.getVersjon(), v);
        assertEquals(inspiser.getType(), SøknadType.ENDRING);
        Endringssøknad respons = Endringssøknad.class.cast(v12XMLMapper.tilSøknad(xml));
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
                v);
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
        Endringssøknad es = endringssøknad(v, VEDLEGG1, VEDLEGG2);
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> konvolutt = konvoluttGenerator.payload(es, person(), v);
        List<HttpEntity<?>> metadata = konvolutt.getBody().get(METADATA);
        List<HttpEntity<?>> vedlegg = konvolutt.getBody().get(VEDLEGG);
        assertEquals(1, metadata.size());
        assertEquals(2, vedlegg.size());
        assertMediaType(vedlegg.get(1), APPLICATION_PDF_VALUE);
        assertMediaType(vedlegg.get(0), APPLICATION_PDF_VALUE);
    }

    private FPFordelKonvoluttGenerator konvoluttGenerator() {
        return new FPFordelKonvoluttGenerator(
                new FPFordelMetdataGenerator(objectMapper),
                v12DomainMapper,
                new ForeldrepengerPDFGenerator(oppslag, fpRenderer));
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
}
