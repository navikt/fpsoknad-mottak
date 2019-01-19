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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

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
import no.nav.foreldrepenger.mottak.domain.felles.VedleggMetaData;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelGosysKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelPendingKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskaper;
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
    public void testPollKvittering() throws Exception {
        test(new FPFordelPendingKvittering(Duration.ofSeconds(6)), true, mapper);
    }

    @Test
    public void testFordeltKvittering() throws Exception {
        test(new FPSakFordeltKvittering("123", "456"), true, mapper);
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
        for (Versjon v : Versjon.alleVersjoner()) {
            test(endringssøknad(v), true);
        }
    }

    @Test
    public void testForeldrepenger() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(foreldrePenger(v, false), true);
        }
    }

    @Test
    public void testSøknad() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(ForeldrepengerTestUtils.søknadMedEttIkkeOpplastedVedlegg(v, false), true);
        }

    }

    @Test
    public void testOpptjening() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(opptjening(v));
        }

    }

    @Test
    public void testRettigheter() {
        for (Versjon v : Versjon.alleVersjoner()) {
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
        test(new SøknadMetadata(new SøknadEgenskaper(SøknadType.INITIELL, Versjon.V1), "42"), true);
    }

    @Test
    public void testSøknadInspeksjon() {
        test(new SøknadEgenskaper(SøknadType.INITIELL, Versjon.V1), true);
    }

    @Test
    public void testVedleggMetadata() {
        test(new VedleggMetaData("42", InnsendingsType.LASTET_OPP, DokumentType.I000002));
    }

    @Test
    public void testUtenlandskForelder() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(utenlandskForelder(v));
        }
    }

    @Test
    public void testNorskForelder() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(norskForelder(v));
        }

    }

    @Test
    public void testFordeling() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(fordeling(v));
        }
    }

    @Test
    public void testUttaksPeride() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(uttaksPeriode(v), true);
        }
    }

    @Test
    public void testGradertPeriode() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(gradertPeriode(v), true);
        }
    }

    @Test
    public void testOverføringsperiode() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(overføringsPeriode(v), true);
        }
    }

    @Test
    public void testOppholdsPeriode() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(oppholdsPeriode(v));
        }
    }

    @Test
    public void testUtsettelsesPeriode() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(utsettelsesPeriode(v));
        }

    }

    @Test
    public void testÅpenPeriode() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(åpenPeriode(v));
        }
    }

    @Test
    public void testAdopsjon() {
        test(adopsjon());
    }

    @Test
    public void testOmsorgsovertagelsse() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(omsorgsovertakelse(v));
        }

    }

    @Test
    public void testTermin() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(termin(v));
        }
    }

    @Test
    public void testAnnenOpptjening() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(annenOpptjening(v));
        }
    }

    @Test
    public void testUtenlandskrbeidsforhold() {
        for (Versjon v : Versjon.alleVersjoner()) {
            test(utenlandskArbeidsforhold(v), true);
        }
    }

    @Test
    public void testEgenNæringUtenlandskorganisasjon() throws Exception {
        ClassPathResource res = new ClassPathResource("utenlandskOrg.json");
        UtenlandskOrganisasjon org = mapper.readValue(res.getInputStream(), UtenlandskOrganisasjon.class);
        assertEquals(CountryCode.UG, org.getRegistrertILand());
        for (Versjon v : Versjon.alleVersjoner()) {
            test(utenlandskEgenNæring(v), true);
        }
    }

    @Test
    public void testEgenNæringNorskorganisasjon() {
        for (Versjon v : Versjon.alleVersjoner()) {
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
