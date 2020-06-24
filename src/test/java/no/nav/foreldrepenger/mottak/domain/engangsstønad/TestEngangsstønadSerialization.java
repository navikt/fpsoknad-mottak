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

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
public class TestEngangsstønadSerialization {

    @Inject
    ObjectMapper mapper;
    private static final Logger LOG = LoggerFactory.getLogger(TestEngangsstønadSerialization.class);

    @Test
    public void testKvittering() {
        Kvittering kvittering = new Kvittering(LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK);
        kvittering.setJournalId("555");
        kvittering.setSaksNr("666");
        test(kvittering, false);
    }

    @Test
    public void testVedlegg() {
        test(påkrevdVedlegg("pdf/terminbekreftelse.pdf"), false);
    }

    @Test
    public void testSøknadNorge() throws Exception {
        Søknad engangssøknad = engangssøknad(false, fødsel(), norskForelder(),
                påkrevdVedlegg(ForeldrepengerTestUtils.ID142));
        test(engangssøknad, false);
    }

    @Test
    public void testEngangsstønadNorge() throws Exception {
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
        test(TestUtils.medlemsskap(false));
    }

    @Test
    public void testMedlemsskapUtland() {
        test(TestUtils.medlemsskap(false));
    }

    @Test
    public void testFnr() {
        test(new Fødselsnummer("03016536325"), false);
    }

    @Test
    public void testAktør() {
        test(new AktørId("111111111"), false);
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
