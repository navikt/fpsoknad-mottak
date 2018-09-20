package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknad;
import static no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter.MULTIPART_MIXED_VALUE;
import static no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelKonvoluttGenerator.HOVEDDOKUMENT;
import static no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelKonvoluttGenerator.METADATA;
import static no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelKonvoluttGenerator.VEDLEGG;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import java.time.Duration;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.mottak.config.CustomSerializerModule;
import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.http.Oppslag;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelGosysKvittering;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelKvittering;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelMetdataGenerator;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelPendingKvittering;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPSakFordeltKvittering;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.ForeldrepengerSøknadMapper;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureJsonTesters

public class TestFPFordelSerialization {

    @Mock
    private Oppslag oppslag;

    private static final AktorId AKTØRID = new AktorId("1111111111");
    private static final Fødselsnummer FNR = new Fødselsnummer("01010111111");

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
        when(oppslag.getAktørId(eq(FNR))).thenReturn(AKTØRID);
        when(oppslag.getFnr(eq(AKTØRID))).thenReturn(FNR);

    }

    @Test
    public void testGosysKvittering() throws Exception {
        FPFordelKvittering kvittering = new FPFordelGosysKvittering("42");
        TestForeldrepengerSerialization.test(kvittering, true, mapper);
    }

    @Test
    public void testPollKvittering() throws Exception {
        FPFordelKvittering kvittering = new FPFordelPendingKvittering(Duration.ofSeconds(6));
        TestForeldrepengerSerialization.test(kvittering, true, mapper);
    }

    @Test
    public void testFordeltKvittering() throws Exception {
        FPFordelKvittering kvittering = new FPSakFordeltKvittering("123", "456");
        TestForeldrepengerSerialization.test(kvittering, true, mapper);
    }

    @Test
    public void testSøknad() throws Exception {
        AktorId aktørId = new AktorId("42");
        ForeldrepengerSøknadMapper fpFordelSøknadGenerator = new ForeldrepengerSøknadMapper(oppslag);
        Søknad original = ForeldrepengerTestUtils.foreldrepenger();
        String xml = fpFordelSøknadGenerator.tilXML(original, aktørId);
        System.out.println(xml);
        Søknad rekonstruert = fpFordelSøknadGenerator.tilSøknad(xml);
        assertThat(rekonstruert.getBegrunnelseForSenSøknad()).isEqualTo(original.getBegrunnelseForSenSøknad());
        assertThat(rekonstruert.getSøker()).isEqualTo(original.getSøker());
        assertThat(rekonstruert.getTilleggsopplysninger()).isEqualTo(original.getTilleggsopplysninger());
        Foreldrepenger originalYtelse = Foreldrepenger.class.cast(original.getYtelse());
        Foreldrepenger rekonstruertYtelse = Foreldrepenger.class.cast(rekonstruert.getYtelse());
        assertThat(rekonstruertYtelse.getDekningsgrad()).isEqualTo(originalYtelse.getDekningsgrad());
        assertThat(rekonstruertYtelse.getRelasjonTilBarn()).isEqualTo(originalYtelse.getRelasjonTilBarn());
        assertThat(rekonstruertYtelse.getAnnenForelder()).isEqualTo(originalYtelse.getAnnenForelder());

        // assertThat(rekonstruertYtelse.getFordeling()).isEqualTo(originalYtelse.getFordeling());

        // assertEquals(original, rekonstruert);
    }

    @Test
    public void testKonvolutt() throws Exception {
        MottakConfiguration mottakConfiguration = new MottakConfiguration();
        FPFordelKonvoluttGenerator konvoluttGenerator = new FPFordelKonvoluttGenerator(
                new FPFordelMetdataGenerator(mapper),
                new ForeldrepengerSøknadMapper(oppslag),
                new ForeldrepengerPDFGenerator(mottakConfiguration.landkoder(),
                        mottakConfiguration.kvitteringstekster()));
        Søknad søknad = søknad(valgfrittVedlegg());
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> konvolutt = konvoluttGenerator.payload(søknad, person(),
                new CallIdGenerator("jalla").create());
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
        MottakConfiguration mottakConfiguration = new MottakConfiguration();
        FPFordelKonvoluttGenerator konvoluttGenerator = new FPFordelKonvoluttGenerator(
                new FPFordelMetdataGenerator(mapper),
                new ForeldrepengerSøknadMapper(oppslag),
                new ForeldrepengerPDFGenerator(mottakConfiguration.landkoder(),
                        mottakConfiguration.kvitteringstekster()));
        ValgfrittVedlegg v1 = new ValgfrittVedlegg(DokumentType.I500002,
                new ClassPathResource("terminbekreftelse.pdf"));
        ValgfrittVedlegg v2 = new ValgfrittVedlegg(DokumentType.I500005,
                new ClassPathResource("terminbekreftelse.pdf"));
        Ettersending es = new Ettersending("42", v1, v2);
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> konvolutt = konvoluttGenerator.payload(es, person(),
                new CallIdGenerator("jalla").create());
        List<HttpEntity<?>> metadata = konvolutt.getBody().get(METADATA);
        assertEquals(1, metadata.size());
        List<HttpEntity<?>> vedlegg = konvolutt.getBody().get(VEDLEGG);
        assertEquals(2, vedlegg.size());
        assertEquals(null, konvolutt.getBody().get(HOVEDDOKUMENT));
        assertMediaType(vedlegg.get(1), APPLICATION_PDF_VALUE);
        assertMediaType(vedlegg.get(0), APPLICATION_PDF_VALUE);
    }

    private static void assertMediaType(HttpEntity<?> entity, String type) {
        assertEquals(entity.getHeaders().get(CONTENT_TYPE).get(0), type);
    }
}
