package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.Collections;

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
import no.nav.foreldrepenger.mottak.innsending.fpinfo.BehandlingsStatus;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoBehandling;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoBehandlingsTema;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoBehandlingsType;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoBehandlingsÅrsaakType;
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
        // mapper.setSerializationInclusion(NON_NULL);
        // mapper.setSerializationInclusion(NON_EMPTY);
        return mapper;
    }

    @Test
    public void testFPInfoKvittering() throws Exception {
        FPInfoSakStatus status = new FPInfoSakStatus("42", FPInfoFagsakStatus.LOP, FPInfoFagsakÅrsak.TERM,
                FPInfoFagsakYtelseType.FP, "1", "2", "3");
        TestForeldrepengerSerialization.test(status, true, mapper);
    }

    @Test
    public void testBehandlingsStatus() throws Exception {
        FPInfoBehandling behandling = new FPInfoBehandling(BehandlingsStatus.AVSLU, FPInfoBehandlingsType.BT002,
                FPInfoBehandlingsTema.ENGST,
                FPInfoBehandlingsÅrsaakType.RE_OPPLYSNINGER_OM_BEREGNINGSGRUNNLAG, "402", "NAV",
                Collections.emptyList());
        TestForeldrepengerSerialization.test(behandling, true, mapper);
    }

    @Test
    public void testBehandlingsType() throws Exception {
        TestForeldrepengerSerialization.test(FPInfoBehandlingsÅrsaakType.BERØRT_BEHANDLING, true, mapper);
        TestForeldrepengerSerialization.test(FPInfoBehandlingsType.BT002, true, mapper);

    }

}
