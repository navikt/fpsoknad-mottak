package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.serialize;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.adopsjon;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.annenOpptjening;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.fordeling;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrePenger;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.gradertPeriode;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.norskEgenNæring;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.omsorgsovertakelse;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.oppholdsPeriode;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.opptjening;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.overføringsPeriode;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.rettigheter;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.termin;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.utenlandskArbeidsforhold;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.utenlandskEgenNæring;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.utenlandskForelder;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.utsettelsesPeriode;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.uttaksPeriode;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.åpenPeriode;
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
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.mottak.config.CustomSerializerModule;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.domain.felles.VedleggMetaData;

@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureJsonTesters
public class TestForeldrepengerSerialization {

    private static final Logger LOG = LoggerFactory.getLogger(TestForeldrepengerSerialization.class);

    @Autowired
    ObjectMapper mapper;

    @Before
    public void init() {
        mapper.registerModule(new CustomSerializerModule());
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
    }

    @Test
    public void testPerson() {
        test(TestUtils.person());
    }

    @Test
    public void testEttersending() throws Exception {
        test(new Ettersending("42", ForeldrepengerTestUtils.ETT_VEDLEGG));
    }

    @Test
    public void testSøknad() {
        test(ForeldrepengerTestUtils.foreldrepenger(), true);
    }

    @Test
    public void testForeldrepenger() {
        test(foreldrePenger());
    }

    @Test
    public void testOpptjening() {
        test(opptjening());
    }

    @Test
    public void testRettigheter() {
        test(rettigheter());
    }

    @Test
    public void testUkjentForelder() {
        test(new UkjentForelder());
    }

    @Test
    public void testVedleggMetadata() {
        test(new VedleggMetaData("42", DokumentType.I000002));
    }

    @Test
    public void testUtenlandskForelder() {
        test(utenlandskForelder());
    }

    @Test
    public void testNorskForelder() {
        test(norskForelder());
    }

    @Test
    public void testFordeling() {
        test(fordeling());
    }

    @Test
    public void testUttaksPeride() {
        test(uttaksPeriode());
    }

    @Test
    public void testGradertPeriode() {
        test(gradertPeriode());
    }

    @Test
    public void testOverføringsperiode() {
        test(overføringsPeriode());
    }

    @Test
    public void testOppholdsPeriode() {
        test(oppholdsPeriode());
    }

    @Test
    public void testUtsettelsesPeriode() {
        test(utsettelsesPeriode());
    }

    @Test
    public void testÅpenPeriode() {
        test(åpenPeriode());
    }

    @Test
    public void testAdopsjon() {
        test(adopsjon());
    }

    @Test
    public void testOmsorgsovertagelsse() {
        test(omsorgsovertakelse());
    }

    @Test
    public void testTermin() {
        test(termin());
    }

    @Test
    public void testAnnenOpptjening() {
        test(annenOpptjening());
    }

    @Test
    public void testUtenlandskrbeidsforhold() {
        test(utenlandskArbeidsforhold());
    }

    @Test
    public void testEgenNæringUtenlandskorganisasjon() {
        test(utenlandskEgenNæring());
    }

    @Test
    public void testEgenNæringNorskorganisasjon() {
        test(norskEgenNæring());
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

}
