package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.adopsjon;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.serialize;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.annenOpptjening;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ettersending;
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
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.config.CustomSerializerModule;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.VedleggMetaData;
import no.nav.foreldrepenger.mottak.util.Versjon;

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
    public void testPeriodeEnDag() {
        LukketPeriodeMedVedlegg periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 1),
                LocalDate.of(2019, Month.MARCH, 1), Collections.emptyList()) {
        };
        assertEquals(1, periode.dager());

    }

    @Test
    public void testPeriodeOverHelga() {
        LukketPeriodeMedVedlegg periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 1),
                LocalDate.of(2019, Month.MARCH, 4), Collections.emptyList()) {
        };
        assertEquals(2, periode.dager());
    }

    @Test
    public void testPeriodeOverToHelger() {
        LukketPeriodeMedVedlegg periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 1),
                LocalDate.of(2019, Month.MARCH, 11), Collections.emptyList()) {
        };
        assertEquals(7, periode.dager());
    }

    @Test
    public void testPerson() {
        test(person());
    }

    @Test
    public void testEttersending() throws Exception {
        test(ettersending(), true);
    }

    @Test
    public void testEndringssøknad() {
        for (Versjon v : Versjon.concreteValues()) {
            test(endringssøknad(v), true);
        }

        // test(endringssøknad(V2), true);
    }

    @Test
    public void testForeldrepenger() {
        for (Versjon v : Versjon.concreteValues()) {
            test(foreldrePenger(v, false), true);
        }
    }

    @Test
    public void testSøknad() {
        for (Versjon v : Versjon.concreteValues()) {
            test(ForeldrepengerTestUtils.søknadMedEttIkkeOpplastedVedlegg(v, false), true);
        }

    }

    @Test
    public void testOpptjening() {
        for (Versjon v : Versjon.concreteValues()) {
            test(opptjening(v));
        }

    }

    @Test
    public void testRettigheter() {
        for (Versjon v : Versjon.concreteValues()) {
            test(rettigheter(v));
        }
    }

    @Test
    public void testUkjentForelder() {
        test(new UkjentForelder());
    }

    @Test
    public void testVedleggMetadata() {
        test(new VedleggMetaData("42", InnsendingsType.LASTET_OPP, DokumentType.I000002));
    }

    @Test
    public void testUtenlandskForelder() {
        for (Versjon v : Versjon.concreteValues()) {
            test(utenlandskForelder(v));
        }
    }

    @Test
    public void testNorskForelder() {
        for (Versjon v : Versjon.concreteValues()) {
            test(norskForelder(v));
        }

    }

    @Test
    public void testFordeling() {
        for (Versjon v : Versjon.concreteValues()) {
            test(fordeling(v));
        }
    }

    @Test
    public void testUttaksPeride() {
        for (Versjon v : Versjon.concreteValues()) {
            test(uttaksPeriode(v), true);
        }
    }

    @Test
    public void testGradertPeriode() {
        for (Versjon v : Versjon.concreteValues()) {
            test(gradertPeriode(v), true);
        }
    }

    @Test
    public void testOverføringsperiode() {
        for (Versjon v : Versjon.concreteValues()) {
            test(overføringsPeriode(v), true);
        }
    }

    @Test
    public void testOppholdsPeriode() {
        for (Versjon v : Versjon.concreteValues()) {
            test(oppholdsPeriode(v));
        }
    }

    @Test
    public void testUtsettelsesPeriode() {
        for (Versjon v : Versjon.concreteValues()) {
            test(utsettelsesPeriode(v));
        }

    }

    @Test
    public void testÅpenPeriode() {
        for (Versjon v : Versjon.concreteValues()) {
            test(åpenPeriode(v));
        }
    }

    @Test
    public void testAdopsjon() {
        test(adopsjon());
    }

    @Test
    public void testOmsorgsovertagelsse() {
        for (Versjon v : Versjon.concreteValues()) {
            test(omsorgsovertakelse(v));
        }

    }

    @Test
    public void testTermin() {
        for (Versjon v : Versjon.concreteValues()) {
            test(termin(v));
        }
    }

    @Test
    public void testAnnenOpptjening() {
        for (Versjon v : Versjon.concreteValues()) {
            test(annenOpptjening(v));
        }
    }

    @Test
    public void testUtenlandskrbeidsforhold() {
        for (Versjon v : Versjon.concreteValues()) {
            test(utenlandskArbeidsforhold(v), true);
        }
    }

    @Test
    public void testEgenNæringUtenlandskorganisasjon() throws Exception {
        ClassPathResource res = new ClassPathResource("utenlandskOrg.json");
        UtenlandskOrganisasjon org = mapper.readValue(res.getInputStream(), UtenlandskOrganisasjon.class);
        assertEquals(CountryCode.UG, org.getRegistrertILand());
        for (Versjon v : Versjon.concreteValues()) {
            test(utenlandskEgenNæring(v), true);
        }
    }

    @Test
    public void testEgenNæringNorskorganisasjon() {
        for (Versjon v : Versjon.concreteValues()) {
            test(norskEgenNæring(v));
        }
    }

    private void test(Object object, boolean print) {
        test(object, print, mapper);
    }

    void test(Object object) {
        test(object, false);

    }

    public static void test(Object expected, boolean log, ObjectMapper mapper) {
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
