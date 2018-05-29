package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.mottak.config.CustomSerializerModule;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.UUIDIdGenerator;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelManuellKvittering;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelMetdataGenerator;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelSøknadGenerator;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureJsonTesters
public class TestFPFordelSerialization {

    private static final ObjectMapper mapper = mapper();

    @Mock
    ForeldrepengerPDFGenerator pdfGenerator;

    @Mock
    FPFordelSøknadGenerator søknadGenerator;

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
    public void testManuellKvittering() throws Exception {
        TestForeldrepengerSerialization.test(new FPFordelManuellKvittering("42"), true, mapper);
    }

    @Test
    public void testSøknad() throws Exception {
        AktorId aktørId = new AktorId("42");
        Søknad søknad = ForeldrepengerTestUtils.søknad();
        FPFordelSøknadGenerator generator = new FPFordelSøknadGenerator();
        String xml = generator.toXML(søknad, aktørId);
        // Soeknad model = generator.toFPFordelModel(søknad, aktørId);
        System.out.println(xml);
    }

    @Test
    public void testKonvolutt() throws Exception {
        when(pdfGenerator.generate(any(Søknad.class))).thenReturn(new String("42").getBytes());
        FPFordelKonvoluttGenerator konvoluttGenerator = new FPFordelKonvoluttGenerator(
                new FPFordelMetdataGenerator(mapper),
                new FPFordelSøknadGenerator(), pdfGenerator);

        Søknad søknad = ForeldrepengerTestUtils.søknad();
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> konvolutt = konvoluttGenerator.payload(søknad,
                new AktorId("42"), new UUIDIdGenerator("jalla").create());
        System.out.println(konvolutt);

    }

}
