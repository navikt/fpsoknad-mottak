package no.nav.foreldrepenger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.lookup.rest.fpinfo.BehandlingsStatus;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoBehandling;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoBehandlingsTema;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoBehandlingsType;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoBehandlingsÅrsakType;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoFagsakStatus;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoFagsakYtelseType;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoFagsakÅrsak;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoSakStatus;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureJsonTesters

public class TestFPInfoSerialization {

    private static final Logger LOG = LoggerFactory.getLogger(TestFPInfoSerialization.class);

    private static final ObjectMapper mapper = mapper();

    private static ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        return mapper;
    }

    @Test
    public void testFPInfoKvittering() throws Exception {
        FPInfoSakStatus status = new FPInfoSakStatus("42", FPInfoFagsakStatus.LOP, FPInfoFagsakÅrsak.TERM,
                FPInfoFagsakYtelseType.FP, "1", "2", "3");
        test(status, true, mapper);
    }

    @Test
    public void testBehandlingsStatus() throws Exception {
        FPInfoBehandling behandling = new FPInfoBehandling(BehandlingsStatus.AVSLU, FPInfoBehandlingsType.BT002,
                FPInfoBehandlingsTema.ENGST,
                FPInfoBehandlingsÅrsakType.RE_OPPLYSNINGER_OM_BEREGNINGSGRUNNLAG, "402", "NAV",
                Collections.emptyList());
        test(behandling, true, mapper);
    }

    @Test
    public void testBehandlingsType() throws Exception {
        test(FPInfoBehandlingsÅrsakType.BERØRT_BEHANDLING, true, mapper);
        test(FPInfoBehandlingsType.BT002, true, mapper);

    }

    private void test(Object object, boolean print) {
        test(object, print, mapper);
    }

    void test(Object object) {
        test(object, true);

    }

    static void test(Object expected, boolean log, ObjectMapper mapper) {
        try {
            String serialized = serialize(expected, log, mapper);
            Object deserialized = mapper.readValue(serialized, expected.getClass());
            if (log) {
                LOG.info("{}", expected);
                LOG.info("{}", serialized);
                LOG.info("{}", deserialized);
            }
            assertEquals(expected, deserialized);
        } catch (IOException e) {
            LOG.error("{}", e);
            fail(expected.getClass().getSimpleName() + " failed");
        }
    }

    public static String serialize(Object obj, boolean print, ObjectMapper mapper) throws JsonProcessingException {
        String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        return print ? printSerialized(serialized) : serialized;
    }

    static String printSerialized(String serialized) {
        return serialized;
    }

}
