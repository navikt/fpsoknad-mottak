package no.nav.foreldrepenger.oppslag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

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
import no.nav.foreldrepenger.mottak.domain.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.Fødsel;
import no.nav.foreldrepenger.mottak.domain.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.OmsorgsOvertakelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.Vedlegg;

public class TestSerialization {

    private static ObjectMapper mapper;
    private static XmlMapper xmlMapper;

    @BeforeClass
    public static void beforeClass() {
        mapper = new ObjectMapper();
        configureMapper(mapper);
        xmlMapper = new XmlMapper();
        configureMapper(xmlMapper);
        // xmlMapper.set
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
        test(vedlegg());
    }

    @Test
    public void testSøknadNorge() {
        test(engangssøknad(false), true);
    }

    @Test
    public void testSøknadUtland() {
        test(engangssøknad(true), true);
    }

    @Test
    public void testEngangsstønadNorge() {
        test(engangstønad(false), true);
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
    public void testAdopsjon() {
        test(adopsjon());
    }

    @Test
    public void testFødsel() {
        test(fødsel());
    }

    @Test
    public void testFremtidigOpphold() {
        test(framtidigOppholdINorge());
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
        test(utenlandsopphold());
    }

    @Test
    public void testVarighet() {
        test(varighet());
    }

    private static Søknad engangssøknad(boolean utland) {
        Søknad s = new Søknad(nå(), søker(), engangstønad(utland));
        s.setBegrunnelseForSenSøknad("Glemte hele greia");
        s.setTilleggsopplysninger("Intet å tilføye");
        return s;
    }

    private static Engangsstønad engangstønad(boolean utland) {
        Engangsstønad stønad = new Engangsstønad(medlemsskap(utland), fremtidigFødsel());
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
        return medlemsskap(false);
    }

    private static Medlemsskap medlemsskap(boolean utland) {
        if (utland) {
            return new Medlemsskap(tidligereOppHoldIUtlandetHeleåret(), framtidigOppholdINorge());
        }
        return new Medlemsskap(tidligereOppHoldINorge(), framtidigOppholdINorge());
    }

    private static TidligereOppholdsInformasjon tidligereOppHoldIUtlandetHeleåret() {
        return new TidligereOppholdsInformasjon(false, ArbeidsInformasjon.ARBEIDET_I_UTLANDET,
                Collections.singletonList(new Utenlandsopphold(CountryCode.SE)));
    }

    private static TidligereOppholdsInformasjon tidligereOppHoldINorge() {
        return new TidligereOppholdsInformasjon(true, ArbeidsInformasjon.ARBEIDET_I_NORGE, Collections.emptyList());
    }

    private static Omsorgsovertakelse omsorgsovertakelse() {
        Omsorgsovertakelse overtakelse = new Omsorgsovertakelse(nå(), OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE);
        overtakelse.setBeskrivelse("beskrivelse");
        overtakelse.setFødselsdato(forrigeMåned());
        return overtakelse;
    }

    private static Vedlegg vedlegg() throws IOException {
        return new ValgfrittVedlegg("vedlegg", new ClassPathResource("test.txt"));
    }

    private static Adopsjon adopsjon() {
        return new Adopsjon(nå(), false);
    }

    private static Fødsel fødsel() {
        Fødsel fødsel = new Fødsel(forrigeMåned());
        return fødsel;
    }

    private static FramtidigOppholdsInformasjon framtidigOppholdINorge() {
        return new FramtidigOppholdsInformasjon(true, true);
    }

    private static Søker søker() {
        return new Søker(aktoer(), BrukerRolle.MOR);
    }

    private static FremtidigFødsel fremtidigFødsel() {
        return new FremtidigFødsel(nå(), nesteMåned());
    }

    private static LukketPeriode varighet() {
        return new LukketPeriode(nå(), nesteMåned());
    }

    private static LocalDate nesteMåned() {
        return nå().plus(enMåned());
    }

    private static LocalDate forrigeMåned() {
        return nå().minus(enMåned());
    }

    private static Period enMåned() {
        return Period.ofMonths(1);
    }

    private static LocalDate nå() {
        return LocalDate.now();
    }

    private static LocalDate ettÅrSiden() {
        return LocalDate.now().minus(Period.ofYears(1));
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
            if (print) {
                System.out.println(deserialized);
            }
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
