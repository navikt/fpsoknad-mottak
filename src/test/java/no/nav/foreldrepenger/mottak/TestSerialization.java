package no.nav.foreldrepenger.mottak;

import static no.nav.foreldrepenger.mottak.TestUtils.adopsjon;
import static no.nav.foreldrepenger.mottak.TestUtils.aktoer;
import static no.nav.foreldrepenger.mottak.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.TestUtils.engangstønad;
import static no.nav.foreldrepenger.mottak.TestUtils.framtidigOppHoldIUtlandet;
import static no.nav.foreldrepenger.mottak.TestUtils.framtidigOppholdINorge;
import static no.nav.foreldrepenger.mottak.TestUtils.fremtidigFødsel;
import static no.nav.foreldrepenger.mottak.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.TestUtils.medlemsskap;
import static no.nav.foreldrepenger.mottak.TestUtils.navnUtenMellomnavn;
import static no.nav.foreldrepenger.mottak.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.TestUtils.omsorgsovertakelse;
import static no.nav.foreldrepenger.mottak.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.TestUtils.serialize;
import static no.nav.foreldrepenger.mottak.TestUtils.søker;
import static no.nav.foreldrepenger.mottak.TestUtils.ukjentForelder;
import static no.nav.foreldrepenger.mottak.TestUtils.utenlandskForelder;
import static no.nav.foreldrepenger.mottak.TestUtils.utenlandsopphold;
import static no.nav.foreldrepenger.mottak.TestUtils.varighet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.config.CustomSerializerModule;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSendingsResultat;

@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureJsonTesters
public class TestSerialization {

    @Autowired
    ObjectMapper mapper;

    @Before
    public void init() {
        mapper.registerModule(new CustomSerializerModule());
    }

    @Test
    public void testResult() {
        SøknadSendingsResultat result = new SøknadSendingsResultat("42");
        test(result, true);
    }

    @Test
    public void testVedlegg() throws IOException {
        test(påkrevdVedlegg("terminbekreftelse.pdf"), true);
    }

    @Test
    public void testSøknadNorge() throws Exception {
        Søknad engangssøknad = engangssøknad(false, fødsel(), norskForelder(), påkrevdVedlegg());
        test(engangssøknad, true);
    }

    @Test
    public void testEngangsstønadNorge() {
        Engangsstønad engangstønad = engangstønad(false, fremtidigFødsel(), norskForelder());
        test(engangstønad, true);
    }

    @Test
    public void testEngangsstønadUtland() {
        test(engangstønad(true, fremtidigFødsel(), utenlandskForelder()), true);
    }

    @Test
    public void testEngangsstønadUkjentFar() {
        test(engangstønad(true, fremtidigFødsel(), ukjentForelder()), true);
    }

    @Test
    public void testNorskAnnenForelder() {
        test(norskForelder(), true);
    }

    @Test
    public void testUtenlandskAnnenForelder() {
        test(utenlandskForelder(), true);
    }

    @Test
    public void testUkjentForelder() {
        test(ukjentForelder(), true);
    }

    @Test
    public void testMedlemsskap() {
        test(medlemsskap(), true);
    }

    @Test
    public void testMedlemsskapUtland() {
        test(medlemsskap(true));
    }

    @Test
    public void testFnr() throws JsonProcessingException {
        test(new Fødselsnummer("03016536325"), true);
    }

    @Test
    public void testAktør() throws JsonProcessingException {
        test(new AktorId("111111111"), true);
    }

    @Test
    public void testAdopsjon() {
        test(adopsjon());
    }

    @Test
    public void testFødsel() {
        test(fødsel(), true);
    }

    @Test
    public void testFremtidigOppholdNorge() {
        test(framtidigOppholdINorge(), true);
    }

    @Test
    public void testFremtidigOppholdUtland() {
        test(framtidigOppHoldIUtlandet(), true);
    }

    @Test
    public void testOmsorgsovertagkelse() {
        test(omsorgsovertakelse());
    }

    @Test
    public void testSøker() {
        test(søker());
    }

    @Test
    public void testSøkerUtenMellomNavn() {
        test(søker(navnUtenMellomnavn()));
    }

    @Test
    public void testAktorId() {
        test(aktoer());
    }

    @Test
    public void testTermin() {
        test(fremtidigFødsel());
    }

    @Test
    public void testUtenlandsopphold() {
        test(utenlandsopphold(), true);
    }

    @Test
    public void testVarighet() {
        test(varighet());
    }

    private void test(Object object, boolean print) {
        test(object, print, mapper);
    }

    void test(Object object) {
        test(object, false);

    }

    private static void test(Object object, boolean print, ObjectMapper mapper) {
        try {
            String serialized = serialize(object, print, mapper);
            Object deserialized = mapper.readValue(serialized, object.getClass());
            if (print) {
                System.out.println(deserialized);
            }
            assertEquals(object, deserialized);
        } catch (IOException e) {
            e.printStackTrace();
            fail(object.getClass().getSimpleName() + " failed");
        }
    }

}
