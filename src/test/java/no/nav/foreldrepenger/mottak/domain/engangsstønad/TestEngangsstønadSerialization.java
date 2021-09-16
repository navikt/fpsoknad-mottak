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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
class TestEngangsstønadSerialization {

    @Inject
    ObjectMapper mapper;
    private static final Logger LOG = LoggerFactory.getLogger(TestEngangsstønadSerialization.class);

    @Test
    void testKvittering() {
        var kvittering = new Kvittering(LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK);
        kvittering.setJournalId("555");
        kvittering.setSaksNr("666");
        test(kvittering, false);
    }

    @Test
    void testVedlegg() {
        test(påkrevdVedlegg("pdf/terminbekreftelse.pdf"), false);
    }

    @Test
    void testSøknadNorge() throws Exception {
        var engangssøknad = engangssøknad(false, fødsel(), norskForelder(),
                påkrevdVedlegg(ForeldrepengerTestUtils.ID142));
        test(engangssøknad, false);
    }

    @Test
    void testEngangsstønadNorge() throws Exception {
        var engangstønad = engangstønad(false, termin(), norskForelder());
        test(engangstønad, false);
    }

    @Test
    void testEngangsstønadUtland() {
        test(TestUtils.engangstønad(true, termin(), utenlandskForelder()), false);
    }

    @Test
    void testEngangsstønadUkjentFar() {
        test(engangstønad(true, termin(), ukjentForelder()), false);
    }

    @Test
    void testNorskAnnenForelder() {
        test(norskForelder(), false);
    }

    @Test
    void testUtenlandskAnnenForelder() {
        test(utenlandskForelder(), false);
    }

    @Test
    void testUkjentForelder() {
        test(ukjentForelder(), false);
    }

    @Test
    void testMedlemsskap() {
        test(TestUtils.medlemsskap(false));
    }

    @Test
    void testMedlemsskapUtland() {
        test(TestUtils.medlemsskap(false));
    }

    @Test
    void testFnr() {
        test(new Fødselsnummer("03016536325"), true);
    }

    @Test
    void testAktør() {
        test(new AktørId("111111111"), true);
    }

    @Test
    void testAdopsjon() {
        test(TestUtils.adopsjon());
    }

    @Test
    void testFødsel() {
        test(fødsel(), false);
    }

    @Test
    void testFremtidigOppholdNorge() {
        test(TestUtils.framtidigOppholdINorge(), false);
    }

    @Test
    void testFremtidigOppholdUtland() {
        test(TestUtils.framtidigOppHoldIUtlandet(), false);
    }

    @Test
    void testOmsorgsovertagkelse() {
        test(TestUtils.omsorgsovertakelse());
    }

    @Test
    void testSøker() {
        test(TestUtils.søker());
    }

    @Test
    void testAktorId() {
        test(TestUtils.aktoer());
    }

    @Test
    void testTermin() {
        test(termin());
    }

    @Test
    void testUtenlandsopphold() {
        test(TestUtils.utenlandsopphold(), false);
    }

    @Test
    void testVarighet() {
        test(TestUtils.varighet());
    }

    private void test(Object object, boolean print) {
        test(object, print, mapper);
    }

    void test(Object object) {
        test(object, false);

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
            fail(object.getClass().getSimpleName() + " failed");
        }
    }

}
