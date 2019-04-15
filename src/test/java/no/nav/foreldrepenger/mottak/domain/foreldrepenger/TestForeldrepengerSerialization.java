package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.adopsjon;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.alleSøknadVersjoner;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.serialize;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.annenOpptjening;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ettersending;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.fordeling;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepenger;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.felles.VedleggMetaData;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskOrganisasjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelGosysKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelPendingKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.SøknadMetadata;
import no.nav.foreldrepenger.mottak.util.Versjon;

@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
public class TestForeldrepengerSerialization {

    private static final Logger LOG = LoggerFactory.getLogger(TestForeldrepengerSerialization.class);

    @Autowired
    ObjectMapper mapper;

    @Test
    public void testGosysKvittering() throws Exception {
        test(new FPFordelGosysKvittering("42"), true, mapper);
    }

    @Test
    public void testProsentAndel() throws Exception {
        ProsentAndel orig = new ProsentAndel(40.0);
        test(orig, true, mapper);
        ProsentAndel prosent = mapper.readValue("{ \"prosent\" : 40}", ProsentAndel.class);
        assertEquals(orig, prosent);
    }

    @Test
    public void testPollKvittering() throws Exception {
        test(new FPFordelPendingKvittering(Duration.ofSeconds(6)), true, mapper);
    }

    @Test
    public void testFordeltKvittering() throws Exception {
        test(new FPSakFordeltKvittering("123", "456"), true, mapper);
    }

    @Test
    public void testDekningsgrad() throws Exception {
        test(Dekningsgrad.GRAD100, true, mapper);
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
        for (Versjon v : alleSøknadVersjoner()) {
            test(endringssøknad(v), true);
        }
    }

    @Test
    public void testForeldrepenger() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(foreldrepenger(v, false), true);
        }
    }

    @Test
    public void testSøknad() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(ForeldrepengerTestUtils.søknadMedEttIkkeOpplastedVedlegg(v, false), true);
        }

    }

    @Test
    public void testOpptjening() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(opptjening(v));
        }

    }

    @Test
    public void testRettigheter() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(rettigheter(v));
        }
    }

    @Test
    public void testUkjentForelder() {
        test(new UkjentForelder());
    }

    @Test
    public void testStønadskontoType() {
        test(StønadskontoType.IKKE_SATT, true);
    }

    @Test
    public void testSøknadMetadata() {
        test(new SøknadMetadata(new SøknadEgenskap(Versjon.V1, SøknadType.INITIELL_FORELDREPENGER), "42"), true);
    }

    @Test
    public void testSøknadInspeksjon() {
        test(new SøknadEgenskap(Versjon.V1, SøknadType.INITIELL_FORELDREPENGER), true);
    }

    @Test
    public void testVedleggMetadata() {
        test(new VedleggMetaData("42", InnsendingsType.LASTET_OPP, DokumentType.I000002));
    }

    @Test
    public void testUtenlandskForelder() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(utenlandskForelder(v));
        }
    }

    @Test
    public void testNorskForelder() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(norskForelder(v));
        }
    }

    @Test
    public void testFordeling() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(fordeling(v), true);
        }
    }

    @Test
    public void testUttaksPeride() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(uttaksPeriode(v), true);
        }
    }

    @Test
    public void testGradertPeriode() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(gradertPeriode(v), true);
        }
    }

    @Test
    public void testOverføringsperiode() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(overføringsPeriode(v), true);
        }
    }

    @Test
    public void testOppholdsPeriode() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(oppholdsPeriode(v));
        }
    }

    @Test
    public void testUtsettelsesPeriode() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(utsettelsesPeriode(v));
        }

    }

    @Test
    public void testÅpenPeriode() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(åpenPeriode(v));
        }
    }

    @Test
    public void testAdopsjon() {
        test(adopsjon());
    }

    @Test
    public void testOmsorgsovertagelsse() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(omsorgsovertakelse(v));
        }

    }

    @Test
    public void testTermin() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(termin(v));
        }
    }

    @Test
    public void testAnnenOpptjening() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(annenOpptjening(v));
        }
    }

    @Test
    public void testUtenlandskArbeidsforhold() {
        for (Versjon v : alleSøknadVersjoner()) {
            test(utenlandskArbeidsforhold(v), true);
        }
    }

    @Test
    public void testEgenNæringUtenlandskOrganisasjon() throws Exception {
        ClassPathResource res = new ClassPathResource("utenlandskOrg.json");
        UtenlandskOrganisasjon org = mapper.readValue(res.getInputStream(), UtenlandskOrganisasjon.class);
        assertEquals(CountryCode.UG, org.getRegistrertILand());
        for (Versjon v : alleSøknadVersjoner()) {
            test(utenlandskEgenNæring(v), true);
        }
    }

    @Test
    public void testEgenNæringNorskorganisasjon() {
        for (Versjon v : alleSøknadVersjoner()) {
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
        String serialized = serialize(expected, log, mapper);
        if (log) {
            LOG.info("Expected {}", expected);
            LOG.info("Serialized {}", serialized);
        }
        try {
            Object deserialized = mapper.readValue(serialized, expected.getClass());
            if (log) {
                LOG.info("Deserialized {}", deserialized);
            }
            assertEquals(expected, deserialized);
        } catch (IOException e) {
            LOG.error("{}", e);
            fail(expected.getClass().getSimpleName() + " failed");
        }
    }

}
