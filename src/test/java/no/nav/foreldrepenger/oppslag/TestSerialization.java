package no.nav.foreldrepenger.oppslag;

import static no.nav.foreldrepenger.oppslag.TestUtils.adopsjon;
import static no.nav.foreldrepenger.oppslag.TestUtils.aktoer;
import static no.nav.foreldrepenger.oppslag.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.oppslag.TestUtils.engangstønad;
import static no.nav.foreldrepenger.oppslag.TestUtils.framtidigOppHoldIUtlandetHeleåret;
import static no.nav.foreldrepenger.oppslag.TestUtils.framtidigOppholdINorge;
import static no.nav.foreldrepenger.oppslag.TestUtils.fremtidigFødsel;
import static no.nav.foreldrepenger.oppslag.TestUtils.fødsel;
import static no.nav.foreldrepenger.oppslag.TestUtils.medlemsskap;
import static no.nav.foreldrepenger.oppslag.TestUtils.norskForelder;
import static no.nav.foreldrepenger.oppslag.TestUtils.omsorgsovertakelse;
import static no.nav.foreldrepenger.oppslag.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.oppslag.TestUtils.søker;
import static no.nav.foreldrepenger.oppslag.TestUtils.utenlandskForelder;
import static no.nav.foreldrepenger.oppslag.TestUtils.utenlandsopphold;
import static no.nav.foreldrepenger.oppslag.TestUtils.varighet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.Fodselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;

public class TestSerialization {

    private static final DokmotEngangsstønadXMLGenerator DOKMOT_ENGANGSSTØNAD_XML_GENERATOR = new DokmotEngangsstønadXMLGenerator();
    private static ObjectMapper mapper;

    @BeforeClass
    public static void beforeClass() {
        mapper = new ObjectMapper();
        configureMapper(mapper);
    }

    private static void configureMapper(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    @Test
    public void testVedlegg() throws IOException {
        test(påkrevdVedlegg("vedlegg.pdf"), true);
    }

    @Test
    public void testSøknadNorge() throws Exception {
        Søknad engangssøknad = engangssøknad(false);
        test(engangssøknad, true);
        System.out.println(DOKMOT_ENGANGSSTØNAD_XML_GENERATOR.toXML(engangssøknad));
    }

    @Test
    public void testSøknadUtland() throws Exception {
        Søknad engangssøknad = engangssøknad(true, fødsel());
        test(engangssøknad, true);
        System.out.println(DOKMOT_ENGANGSSTØNAD_XML_GENERATOR.toXML(engangssøknad));

    }

    @Test
    public void testEngangsstønadNorge() {
        Engangsstønad engangstønad = engangstønad(false);
        test(engangstønad, true);
    }

    @Test
    public void testEngangsstønadUtland() {
        test(engangstønad(true));
    }

    @Test
    public void testNorskAnnenForelder() {
        test(norskForelder());
    }

    @Test
    public void testUtenlandskAnnenForelder() {
        test(utenlandskForelder());
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
        test(new Fodselsnummer("03016536325"), true);
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
        test(framtidigOppholdINorge());
    }

    @Test
    public void testFremtidigOppholdUtland() {
        test(framtidigOppHoldIUtlandetHeleåret(), true);
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

    private static void test(Object object, boolean print) {
        test(object, print, mapper);
    }

    static void test(Object object) {
        test(object, false);

    }

    private static void test(Object object, boolean print, ObjectMapper mapper) {
        try {
            String serialized = write(object, print, mapper);
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

    static String write(Object obj, boolean print, ObjectMapper mapper) throws JsonProcessingException {
        String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        if (print) {
            System.out.println(serialized);
            return serialized;
        }
        return serialized;
    }

}
