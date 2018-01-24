package no.nav.foreldrepenger.oppslag;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.Fødsel;
import no.nav.foreldrepenger.mottak.domain.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.OmsorgsOvertakelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.OppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.TerminInfo;
import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.Varighet;

public class TestSerialization {

    private static ObjectMapper mapper;
    private static XmlMapper xmlMapper;

    @BeforeClass
    public static void beforeClass() {
        mapper = new ObjectMapper();
        configureMapper(mapper);
        xmlMapper = new XmlMapper();
        configureMapper(xmlMapper);
    }

    private static void configureMapper(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    @Test
    public void testSøknad() {
        test(engangssøknad(), true, xmlMapper);
    }

    private Søknad engangssøknad() {
        return new Søknad(nå(), søker(), engangstønad());
    }

    @Test
    public void testEngangsstønad() {
        test(engangstønad());
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
        test(medlemsskap());
    }

    @Test
    public void testAdopsjon() {
        test(adopsjon());
    }

    @Test
    public void testFødsel() {
        test(fødsel());
    }

    @Test
    public void testNorgesInfo() {
        test(norgesInfo());
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
        test(termin());
    }

    @Test
    public void testUtenlandsopphold() {
        test(utenlandsopphold());
    }

    @Test
    public void testVarighet() {
        test(varighet());
    }

    private static Engangsstønad engangstønad() {
        Engangsstønad stønad = new Engangsstønad(medlemsskap(), fødsel());
        stønad.setAnnenForelder(norskForelder());
        return stønad;
    }

    private static Utenlandsopphold utenlandsopphold() {
        return new Utenlandsopphold(CountryCode.SE, varighet());
    }

    private static NorskForelder norskForelder() {
        return new NorskForelder(true, aktoer());
    }

    private static UtenlandskForelder utenlandskForelder() {
        return new UtenlandskForelder(true, CountryCode.SE);
    }

    private static Medlemsskap medlemsskap() {
        return new Medlemsskap(norgesInfo(), singletonList(utenlandsopphold()), singletonList(varighet()));
    }

    private static Omsorgsovertakelse omsorgsovertakelse() {
        Omsorgsovertakelse overtakelse = new Omsorgsovertakelse(nå(), OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE);
        overtakelse.setBeskrivelse("beskrivelse");
        overtakelse.setFødselsdato(forrigeMåned());
        return overtakelse;
    }

    private static Adopsjon adopsjon() {
        Adopsjon adopsjon = new Adopsjon(nå(), false);
        return adopsjon;
    }

    private static Fødsel fødsel() {
        return new Fødsel(forrigeMåned());
    }

    private static OppholdsInformasjon norgesInfo() {
        return new OppholdsInformasjon(true, true, true);
    }

    private static Søker søker() {
        return new Søker(aktoer(), BrukerRolle.MOR);
    }

    private static TerminInfo termin() {
        return new TerminInfo(nå(), nesteMåned());
    }

    private static Varighet varighet() {
        return new Varighet(nå(), nesteMåned());
    }

    private static LocalDate nesteMåned() {
        return nå().plus(Period.ofWeeks(4));
    }

    private static LocalDate forrigeMåned() {
        return nå().minus(Period.ofWeeks(4));
    }

    private static LocalDate nå() {
        return LocalDate.now();
    }

    private static AktorId aktoer() {
        return new AktorId("11111111111111111");
    }

    private static void test(Object object) {
        test(object, false);

    }

    private static void test(Object object, boolean print) {
        test(object, print, mapper);
    }

    private static void test(Object object, boolean print, ObjectMapper mapper) {
        try {
            String serialized = write(object, print, mapper);
            Object deserialized = mapper.readValue(serialized, object.getClass());
            assertEquals(object, deserialized);
        } catch (IOException e) {
            e.printStackTrace();
            fail(object.getClass().getSimpleName() + " failed");
        }
    }

    private static String write(Object obj, boolean print, ObjectMapper mapper) throws JsonProcessingException {
        String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        if (print) {
            System.out.println(serialized);
            return serialized;
        }
        return serialized;
    }
}
