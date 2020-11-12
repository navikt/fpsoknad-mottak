package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.adopsjon;
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
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.Duration;

import org.assertj.core.util.Lists;
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
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.GosysKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.PendingKvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.SøknadMetadata;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.UtsettelsePeriodeType;
import no.nav.foreldrepenger.mottak.util.Versjon;

@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
public class TestForeldrepengerSerialization {

    private static final Logger LOG = LoggerFactory.getLogger(TestForeldrepengerSerialization.class);

    @Autowired
    ObjectMapper mapper;

    @Test
    public void testGosysKvittering() throws Exception {
        test(new GosysKvittering("42"), false, mapper);
    }

    @Test
    public void testProsentAndel() throws Exception {
        ProsentAndel orig = new ProsentAndel(40.0);
        test(orig, false, mapper);
        assertEquals(orig, mapper.readValue("{ \"p1\" : 40}", ProsentAndel.class));
        assertEquals(orig, mapper.readValue("{ \"p2\" : 40.0}", ProsentAndel.class));
        orig = new ProsentAndel(40);
        test(orig, false, mapper);
        assertEquals(orig, mapper.readValue("{ \"p1\" : 40}", ProsentAndel.class));
        assertEquals(orig, mapper.readValue("{ \"p2\" : 40.0}", ProsentAndel.class));

    }

    @Test
    public void testPollKvittering() throws Exception {
        test(new PendingKvittering(Duration.ofSeconds(6)), false, mapper);
    }

    @Test
    public void testFordeltKvittering() throws Exception {
        test(new FPSakFordeltKvittering("123", "456"), false, mapper);
    }

    @Test
    public void test123() throws Exception {
        test(UtsettelsePeriodeType.FERIE, false, mapper);
    }

    @Test
    public void testDekningsgrad() throws Exception {
        test(Dekningsgrad.GRAD100, false, mapper);
    }

    @Test
    public void testPerson() {
        test(person());
    }

    @Test
    public void testEttersending() throws Exception {
        test(ettersending(), false);
    }

    @Test
    public void testEndringssøknad() {
        for (Versjon v : Lists.newArrayList(DEFAULT_VERSJON)) {
            test(endringssøknad(v), false);
        }
    }

    @Test
    public void testForeldrepenger() {
        for (Versjon v : Lists.newArrayList(DEFAULT_VERSJON)) {
            test(foreldrepenger(v, false), false);
        }
    }

    @Test
    public void testSøknad() {
        for (Versjon v : Lists.newArrayList(DEFAULT_VERSJON)) {
            test(ForeldrepengerTestUtils.søknadMedEttIkkeOpplastedVedlegg(v, false), true);
        }

    }

    @Test
    public void testOpptjening() {
        for (Versjon v : Lists.newArrayList(DEFAULT_VERSJON)) {
            test(opptjening(v));
        }

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
    public void testStønadskontoType() {
        test(StønadskontoType.IKKE_SATT, false);
    }

    @Test
    public void testSøknadMetadata() {
        test(new SøknadMetadata(new SøknadEgenskap(Versjon.V1, SøknadType.INITIELL_FORELDREPENGER), "42"), false);
    }

    @Test
    public void testSøknadInspeksjon() {
        test(new SøknadEgenskap(Versjon.V1, SøknadType.INITIELL_FORELDREPENGER), false);
    }

    @Test
    public void testVedleggMetadata() {
        test(new VedleggMetaData("42", InnsendingsType.LASTET_OPP, DokumentType.I000002));
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
        for (Versjon v : Lists.newArrayList(DEFAULT_VERSJON)) {
            test(fordeling(v), false);
        }
    }

    @Test
    public void testUttaksPeride() {
        test(uttaksPeriode(), false);
    }

    @Test
    public void testGradertPeriode() {
        for (Versjon v : Lists.newArrayList(DEFAULT_VERSJON)) {
            test(gradertPeriode(v), false);
        }
    }

    @Test
    public void testOverføringsperiode() {
        test(overføringsPeriode(), false);
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
        for (Versjon v : Lists.newArrayList(DEFAULT_VERSJON)) {
            test(annenOpptjening(v));
        }
    }

    @Test
    public void testUtenlandskArbeidsforhold() {
        test(utenlandskArbeidsforhold(), false);
    }

    @Test
    public void testEgenNæringUtenlandskOrganisasjon() throws Exception {
        ClassPathResource res = new ClassPathResource("json/utenlandskOrg.json");
        UtenlandskOrganisasjon org = mapper.readValue(res.getInputStream(), UtenlandskOrganisasjon.class);
        assertEquals(CountryCode.UG, org.getRegistrertILand());
        test(utenlandskEgenNæring(), false);
    }

    @Test
    public void testEgenNæringNorskorganisasjon() {
        test(norskEgenNæring());
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
