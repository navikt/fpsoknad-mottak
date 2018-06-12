package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknad;
import static no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelKonvoluttGenerator.HOVEDDOKUMENT;
import static no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelKonvoluttGenerator.METADATA;
import static no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelKonvoluttGenerator.VEDLEGG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import java.time.Duration;
import java.util.Base64;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
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
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelGosysKvittering;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelKvittering;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelMetdataGenerator;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelPendingKvittering;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelSøknadGenerator;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPSakFordeltKvittering;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureJsonTesters

public class TestFPFordelSerialization {

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
        TestForeldrepengerSerialization.test(kvittering, true,
                mapper);

    }

    @Test
    public void testSøknad() throws Exception {
        AktorId aktørId = new AktorId("42");
        FPFordelSøknadGenerator fpFordelSøknadGenerator = new FPFordelSøknadGenerator();
        String xml = fpFordelSøknadGenerator.toXML(ForeldrepengerTestUtils.foreldrepenger(), aktørId);
        System.out.println(xml);
    }

    @Test
    public void testKonvolutt() throws Exception {
        MottakConfiguration mottakConfiguration = new MottakConfiguration();
        FPFordelKonvoluttGenerator konvoluttGenerator = new FPFordelKonvoluttGenerator(
                new FPFordelMetdataGenerator(mapper),
                new FPFordelSøknadGenerator(), new ForeldrepengerPDFGenerator(mottakConfiguration.landkoder(),
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
        assertMediaType(konvolutt, MediaType.parseMediaType("multipart/mixed").toString());
        assertMediaType(metadata.get(0), APPLICATION_JSON_UTF8_VALUE);
        assertMediaType(hoveddokumenter.get(0), APPLICATION_XML_VALUE);
        assertMediaType(hoveddokumenter.get(1), APPLICATION_PDF_VALUE);
        assertTrue(hasPdfSignature(Base64.getDecoder().decode((byte[]) hoveddokumenter.get(1).getBody())));
        assertMediaType(vedlegg.get(0), APPLICATION_PDF_VALUE);
        assertTrue(hasPdfSignature(Base64.getDecoder().decode((byte[]) vedlegg.get(0).getBody())));
    }

    private static void assertMediaType(HttpEntity<?> entity, String type) {
        assertEquals(entity.getHeaders().get(CONTENT_TYPE).get(0), type);
    }

}
