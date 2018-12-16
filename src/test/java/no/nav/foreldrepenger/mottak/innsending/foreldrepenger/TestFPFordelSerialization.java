package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I500002;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I500005;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ID142;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ID143;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknad;
import static no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter.MULTIPART_MIXED_VALUE;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.HOVEDDOKUMENT;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.METADATA;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.VEDLEGG;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.mottak.config.CustomSerializerModule;
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
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.TestForeldrepengerSerialization;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengeInfoRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengerPDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.PDFElementRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.SøknadTextFormatter;
import no.nav.foreldrepenger.mottak.innsyn.V1XMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.V2XMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.XMLMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.DefaultDokumentTypeAnalysator;
import no.nav.foreldrepenger.mottak.util.Versjon;

@RunWith(SpringRunner.class)
@AutoConfigureJsonTesters
@ContextConfiguration(classes = { MottakConfiguration.class, SøknadTextFormatter.class, ForeldrepengeInfoRenderer.class,
        PDFElementRenderer.class,
        ForeldrepengerPDFGenerator.class, TestConfig.class })
public class TestFPFordelSerialization {

    private static final DefaultDokumentTypeAnalysator ANALYSATOR = new DefaultDokumentTypeAnalysator();

    @Mock
    private Oppslag oppslag;

    private FPFordelKonvoluttGenerator konvoluttGenerator;

    @Inject
    PDFElementRenderer pdfRenderer;
    @Inject
    ForeldrepengeInfoRenderer fpRenderer;

    private XMLMapper v1XMLMapper, v2XMLMapper;

    private DomainMapper v1DomainMapper, v2DomainMapper;

    private static final ValgfrittVedlegg VEDLEGG1 = opplastetVedlegg(ID142, I500002);
    private static final ValgfrittVedlegg VEDLEGG2 = opplastetVedlegg(ID143, I500005);

    private static final AktorId AKTØRID = new AktorId("1111111111");
    private static final Fødselsnummer FNR = new Fødselsnummer("01010111111");
    private static final List<Arbeidsforhold> ARB_FORHOLD = arbeidsforhold();

    private static final ObjectMapper mapper = mapper();

    private static ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CustomSerializerModule());
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        mapper.setSerializationInclusion(NON_NULL);
        mapper.setSerializationInclusion(NON_EMPTY);
        return mapper;
    }

    @Before
    public void before() {

        v1XMLMapper = new V1XMLMapper(oppslag);
        v2XMLMapper = new V2XMLMapper(oppslag);
        v1DomainMapper = new V1DomainMapper(oppslag);
        v2DomainMapper = new V2DomainMapper(oppslag);

        konvoluttGenerator = konvoluttGenerator();
        when(oppslag.getAktørId(eq(FNR))).thenReturn(AKTØRID);
        when(oppslag.getFnr(eq(AKTØRID))).thenReturn(FNR);
        when(oppslag.getArbeidsforhold()).thenReturn(ARB_FORHOLD);
    }

    @Test
    public void testGosysKvittering() throws Exception {
        TestForeldrepengerSerialization.test(new FPFordelGosysKvittering("42"), true, mapper);
    }

    @Test
    public void testPollKvittering() throws Exception {
        TestForeldrepengerSerialization.test(new FPFordelPendingKvittering(Duration.ofSeconds(6)), true, mapper);
    }

    @Test
    public void testFordeltKvittering() throws Exception {
        TestForeldrepengerSerialization.test(new FPSakFordeltKvittering("123", "456"), true, mapper);
    }

    @Test
    public void testEndringssøknadRoundtripV1() throws Exception {

        Endringssøknad original = ForeldrepengerTestUtils.endringssøknad(V1, VEDLEGG1, VEDLEGG2);
        String xml = v1DomainMapper.tilXML(original, AKTØRID);
        assertEquals(ANALYSATOR.type(xml), SøknadType.ENDRING);
        assertEquals(ANALYSATOR.versjon(xml), V1);
        // assertEquals(original, v1mapper.tilSøknad(xml));
    }

    @Test
    public void testSøknadRoundtripV1() throws Exception {
        Søknad original = ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg(V1);
        String xml = v1DomainMapper.tilXML(original, AKTØRID);
        assertEquals(ANALYSATOR.versjon(xml), V1);
        assertEquals(original, v1XMLMapper.tilSøknad(xml));
    }

    @Test
    public void testSøknadRoundtripV2() throws Exception {
        VersjonsBevisstDomainMapper dm = new DefaultVersjonsBevisstDomainMapper(v2DomainMapper);
        Søknad original = ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg(V2);
        String xml = v2DomainMapper.tilXML(original, AKTØRID);
        assertEquals(ANALYSATOR.versjon(xml), V2);
        assertEquals(original, v2XMLMapper.tilSøknad(xml));
    }

    @Test
    public void testKonvolutt() throws Exception {
        Søknad søknad = søknad(V1, false,
                valgfrittVedlegg(ForeldrepengerTestUtils.ID142, InnsendingsType.LASTET_OPP));
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> konvolutt = konvoluttGenerator.payload(søknad, person(),
                Versjon.V1);
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

    @Test
    public void testKonvoluttEttersending() throws Exception {
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

    @Test
    public void testKonvoluttEndring() throws Exception {
        Endringssøknad es = ForeldrepengerTestUtils.endringssøknad(V1, VEDLEGG1, VEDLEGG2);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(es));
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> konvolutt = konvoluttGenerator.payload(es, person(),
                Versjon.V1);
        List<HttpEntity<?>> metadata = konvolutt.getBody().get(METADATA);
        assertEquals(1, metadata.size());
        List<HttpEntity<?>> vedlegg = konvolutt.getBody().get(VEDLEGG);
        assertEquals(2, vedlegg.size());
        assertMediaType(vedlegg.get(1), APPLICATION_PDF_VALUE);
        assertMediaType(vedlegg.get(0), APPLICATION_PDF_VALUE);
    }

    private FPFordelKonvoluttGenerator konvoluttGenerator() {
        return new FPFordelKonvoluttGenerator(
                new FPFordelMetdataGenerator(mapper),
                new DefaultVersjonsBevisstDomainMapper(new V1DomainMapper(oppslag)),
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

    private static ArrayList<Arbeidsforhold> arbeidsforhold() {
        return newArrayList(
                new Arbeidsforhold("1234", "", LocalDate.now().minusDays(200),
                        Optional.of(LocalDate.now()), 90.0, "El Bedrifto"));
    }

    private static void assertMediaType(HttpEntity<?> entity, String type) {
        assertEquals(entity.getHeaders().get(CONTENT_TYPE).get(0), type);
    }
}
