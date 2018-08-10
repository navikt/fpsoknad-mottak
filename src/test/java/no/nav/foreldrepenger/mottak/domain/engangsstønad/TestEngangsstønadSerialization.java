package no.nav.foreldrepenger.mottak.domain.engangsstønad;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangstønad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.serialize;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.termin;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.ukjentForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.utenlandskForelder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.mottak.config.CustomSerializerModule;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureJsonTesters
public class TestEngangsstønadSerialization {

    @Autowired
    ObjectMapper mapper;
    private static final Logger LOG = LoggerFactory.getLogger(TestEngangsstønadSerialization.class);

    @Before
    public void init() {
        mapper.registerModule(new CustomSerializerModule());
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    @Test
    public void testKvittering() {
        Kvittering kvittering = new Kvittering(LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK, "42");
        kvittering.setJournalId("555");
        kvittering.setSaksNr("666");
        test(kvittering, true);
    }

    @Test
    public void testVedlegg() throws IOException {
        test(påkrevdVedlegg("terminbekreftelse.pdf"), false);
    }

    @Test
    public void testSøknadNorge() throws Exception {
        Søknad engangssøknad = engangssøknad(false, fødsel(), norskForelder(), påkrevdVedlegg());
        test(engangssøknad, true);
    }

    @Test
    public void testEngangsstønadNorge() {
        Engangsstønad engangstønad = engangstønad(false, termin(), norskForelder());
        test(engangstønad, false);
    }

    @Test
    public void testEngangsstønadUtland() {
        test(TestUtils.engangstønad(true, termin(), utenlandskForelder()), false);
    }

    @Test
    public void testEngangsstønadUkjentFar() {
        test(engangstønad(true, termin(), ukjentForelder()), false);
    }

    @Test
    public void testNorskAnnenForelder() {
        test(norskForelder(), false);
    }

    @Test
    public void testUtenlandskAnnenForelder() {
        test(utenlandskForelder(), false);
    }

    @Test
    public void testUkjentForelder() {
        test(ukjentForelder(), false);
    }

    @Test
    public void testMedlemsskap() {
        test(TestUtils.medlemsskap(), false);
    }

    @Test
    public void testMedlemsskapUtland() {
        test(TestUtils.medlemsskap(true));
    }

    @Test
    public void testFnr() throws JsonProcessingException {
        test(new Fødselsnummer("03016536325"), false);
    }

    @Test
    public void testAktør() throws JsonProcessingException {
        test(new AktorId("111111111"), false);
    }

    @Test
    public void testAdopsjon() {
        test(TestUtils.adopsjon());
    }

    @Test
    public void testFødsel() {
        test(fødsel(), false);
    }

    @Test
    public void testFremtidigOppholdNorge() {
        test(TestUtils.framtidigOppholdINorge(), false);
    }

    @Test
    public void testFremtidigOppholdUtland() {
        test(TestUtils.framtidigOppHoldIUtlandet(), false);
    }

    @Test
    public void testOmsorgsovertagkelse() {
        test(TestUtils.omsorgsovertakelse());
    }

    @Test
    public void testSøker() {
        test(TestUtils.søker());
    }

    @Test
    public void testSøkerUtenMellomNavn() {
        test(TestUtils.søker(TestUtils.navnUtenMellomnavn()));
    }

    @Test
    public void testAktorId() {
        test(TestUtils.aktoer());
    }

    @Test
    public void testTermin() {
        test(termin());
    }

    @Test
    public void testUtenlandsopphold() {
        test(TestUtils.utenlandsopphold(), false);
    }

    @Test
    public void testVarighet() {
        test(TestUtils.varighet());
    }

    private void test(Object object, boolean print) {
        test(object, print, mapper);
    }

    void test(Object object) {
        test(object, true);

    }

    private static void test(Object object, boolean log, ObjectMapper mapper) {
        try {
            String serialized = serialize(object, log, mapper);
            if (log) {
                LOG.info("{}", serialized);
            }
            Object deserialized = mapper.readValue(serialized, object.getClass());
            if (log) {
                LOG.info("{}", deserialized);
            }
            assertEquals(object, deserialized);
        } catch (IOException e) {
            e.printStackTrace();
            fail(object.getClass().getSimpleName() + " failed");
        }
    }

}
