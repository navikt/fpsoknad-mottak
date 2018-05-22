package no.nav.foreldrepenger.mottak.fpfordel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import no.nav.foreldrepenger.mottak.config.CustomSerializerModule;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestMetadata {

    private static final ObjectMapper mapper = mapper();

    @Test
    public void testMetadata() throws Exception {
        FPFordelMetadata metadata = new FPFordelMetadata(ForeldrepengerTestUtils.søknad(), new AktorId("42"), "42");
        String json = new FPFordelMetdataGenerator(mapper).generateMetadata(metadata, true);
        System.out.println(json);
    }

    private static ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JavaTimeModule(), new Jdk8Module(), new CustomSerializerModule());
        return mapper;
    }

}
