package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static no.nav.foreldrepenger.common.util.Versjon.DEFAULT_VERSJON;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;

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

import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.common.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.VedleggMetaData;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.common.domain.felles.opptjening.UtenlandskOrganisasjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.innsending.SøknadType;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.GosysKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.PendingKvittering;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsyn.SøknadMetadata;
import no.nav.foreldrepenger.common.util.Versjon;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.UtsettelsePeriodeType;

@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
public class TestForeldrepengerSerialization {

    private static final Logger LOG = LoggerFactory.getLogger(TestForeldrepengerSerialization.class);

    @Autowired
    ObjectMapper mapper;

    @Test
    void testGosysKvittering() throws Exception {
        test(new GosysKvittering("42"), false, mapper);
    }

    @Test
    void testProsentAndel() throws Exception {
        ProsentAndel orig = new ProsentAndel(40.0);
        ProsentAndel orig1 = new ProsentAndel(40);
        test(orig, false, mapper);
        test(orig1, false, mapper);
        assertEquals(orig, mapper.readValue("{ \"p1\" : 40}", ProsentAndel.class));
        assertEquals(orig, mapper.readValue("{ \"p2\" : 40.0}", ProsentAndel.class));
        orig = new ProsentAndel(40);
        test(orig, false, mapper);
        assertEquals(orig, mapper.readValue("{ \"p1\" : 40}", ProsentAndel.class));
        assertEquals(orig, mapper.readValue("{ \"p2\" : 40.0}", ProsentAndel.class));

    }

    @Test
    void testPollKvittering() throws Exception {
        test(new PendingKvittering(Duration.ofSeconds(6)), false, mapper);
    }

    @Test
    void testFordeltKvittering() throws Exception {
        test(new FPSakFordeltKvittering("123", "456"), false, mapper);
    }

    @Test
    void test123() throws Exception {
        test(UtsettelsePeriodeType.FERIE, false, mapper);
    }

    @Test
    void testDekningsgrad() throws Exception {
        test(Dekningsgrad.GRAD100, false, mapper);
    }

    @Test
    void testPerson() {
        test(person());
    }

    @Test
    void testEttersending() throws Exception {
        test(ettersending(), false);
    }

    @Test
    void testEndringssøknad() {
        test(endringssøknad(DEFAULT_VERSJON), false);
    }

    @Test
    void testForeldrepenger() {
        test(foreldrepenger(DEFAULT_VERSJON, false), false);
    }

    @Test
    void testSøknad() {
        test(ForeldrepengerTestUtils.søknadMedEttIkkeOpplastedVedlegg(DEFAULT_VERSJON, false), true);
    }

    @Test
    void testOpptjening() {
        test(opptjening(DEFAULT_VERSJON));
    }

    @Test
    void testRettigheter() {
        test(rettigheter());
    }

    @Test
    void testUkjentForelder() {
        test(new UkjentForelder());
    }

    @Test
    void testStønadskontoType() {
        test(StønadskontoType.IKKE_SATT, false);
    }

    @Test
    void testSøknadMetadata() {
        test(new SøknadMetadata(new SøknadEgenskap(Versjon.V1, SøknadType.INITIELL_FORELDREPENGER), "42"), false);
    }

    @Test
    void testSøknadInspeksjon() {
        test(new SøknadEgenskap(Versjon.V1, SøknadType.INITIELL_FORELDREPENGER), false);
    }

    @Test
    void testVedleggMetadata() {
        test(new VedleggMetaData("42", InnsendingsType.LASTET_OPP, DokumentType.I000002));
    }

    @Test
    void testUtenlandskForelder() {
        test(utenlandskForelder());
    }

    @Test
    void testNorskForelder() {
        test(norskForelder());
    }

    @Test
    void testFordeling() {
        test(fordeling(DEFAULT_VERSJON), false);

    }

    @Test
    void testUttaksPeride() {
        test(uttaksPeriode(), false);
    }

    @Test
    void testGradertPeriode() {
        test(gradertPeriode(DEFAULT_VERSJON), false);
    }

    @Test
    void testOverføringsperiode() {
        test(overføringsPeriode(), false);
    }

    @Test
    void testOppholdsPeriode() {
        test(oppholdsPeriode());
    }

    @Test
    void testUtsettelsesPeriode() {
        test(utsettelsesPeriode());
    }

    @Test
    void testÅpenPeriode() {
        test(åpenPeriode());
    }

    @Test
    void testAdopsjon() {
        test(adopsjon());
    }

    @Test
    void testOmsorgsovertagelsse() {
        test(omsorgsovertakelse());
    }

    @Test
    void testTermin() {
        test(termin());
    }

    @Test
    void testAnnenOpptjening() {
        test(annenOpptjening(DEFAULT_VERSJON));
    }

    @Test
    void testUtenlandskArbeidsforhold() {
        test(utenlandskArbeidsforhold(), false);
    }

    @Test
    void relasjonTilBarn() {
        RelasjonTilBarn f = new Fødsel(LocalDate.now());
        test(f, true);
        f = new FremtidigFødsel(LocalDate.now(), LocalDate.now());
        test(f, true);
        f = new Adopsjon(1, LocalDate.now(), true, false, null, null, null);
    }

    @Test
    void testEgenNæringUtenlandskOrganisasjon() throws Exception {
        assertEquals(CountryCode.UG, mapper.readValue(new ClassPathResource("json/utenlandskOrg.json").getInputStream(), UtenlandskOrganisasjon.class)
                .getRegistrertILand());
        test(utenlandskEgenNæring(), false);
    }

    @Test
    void testEgenNæringNorskorganisasjon() {
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
