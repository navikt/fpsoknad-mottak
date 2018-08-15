package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.mottak.config.CustomSerializerModule;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoFagsakStatus;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoFagsakYtelseType;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoFagsakÅrsak;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoSakStatus;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureJsonTesters

public class TestFPInfoSerialization {

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
    public void testFPInfoKvittering() throws Exception {
        FPInfoSakStatus status = new FPInfoSakStatus("42", FPInfoFagsakStatus.LOP, FPInfoFagsakÅrsak.TERM,
                FPInfoFagsakYtelseType.FP, AktorId.valueOf("1"), AktorId.valueOf("2"), AktorId.valueOf("3"));
        TestForeldrepengerSerialization.test(status, true, mapper);
    }

}
