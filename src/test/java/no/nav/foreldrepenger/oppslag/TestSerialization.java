package no.nav.foreldrepenger.oppslag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;

import javax.xml.bind.JAXBException;

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
import no.nav.foreldrepenger.mottak.domain.Bruker;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.Fodselsnummer;
import no.nav.foreldrepenger.mottak.domain.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.Fødsel;
import no.nav.foreldrepenger.mottak.domain.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.OmsorgsOvertakelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.Vedlegg;

public class TestSerialization {

    private static final DokmotEngangsstønadXMLGenerator DOKMOT_ENGANGSSTØNAD_XML_GENERATOR = new DokmotEngangsstønadXMLGenerator();
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
        test(vedlegg("vedlegg.pdf"), true);
    }

    @Test
    public void testSøknadNorge() throws JAXBException {
        Søknad engangssøknad = engangssøknad(false);
        test(engangssøknad, true);
        System.out.println(DOKMOT_ENGANGSSTØNAD_XML_GENERATOR.toXML(engangssøknad));
    }

    @Test
    public void testSøknadUtland() {
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

    private static Søknad engangssøknad(boolean utland) {
        return engangssøknad(utland, fremtidigFødsel());
    }

    private static Søknad engangssøknad(boolean utland, RelasjonTilBarn relasjon) {
        Søknad s = new Søknad(nå(), søker(), engangstønad(utland, relasjon));
        s.setBegrunnelseForSenSøknad("Glemte hele ungen");
        s.setTilleggsopplysninger("Intet å tilføye");
        return s;
    }

    private static Engangsstønad engangstønad(boolean utland) {
        Engangsstønad stønad = engangstønad(utland, fremtidigFødsel());
        stønad.setAnnenForelder(norskForelder());
        return stønad;
    }

    private static Engangsstønad engangstønad(boolean utland, RelasjonTilBarn relasjon) {
        return new Engangsstønad(medlemsskap(utland), relasjon);
    }

    private static Utenlandsopphold utenlandsopphold() {
        return new Utenlandsopphold(CountryCode.SE, varighet());
    }

    private static NorskForelder norskForelder() {
        return new NorskForelder(true, fnr());
    }

    private static UtenlandskForelder utenlandskForelder() {
        return new UtenlandskForelder(true, CountryCode.SE);
    }

    private static Medlemsskap medlemsskap() {
        return medlemsskap(false);
    }

    private static Medlemsskap medlemsskap(boolean utland) {
        if (utland) {
            return new Medlemsskap(tidligereOppHoldIUtlandetHeleåret(), framtidigOppHoldIUtlandetHeleåret());
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

    private static Vedlegg vedlegg(String name) throws IOException {
        return new ValgfrittVedlegg(new ClassPathResource(name));
    }

    private static Adopsjon adopsjon() {
        return new Adopsjon(nå(), false);
    }

    private static Fødsel fødsel() {
        Fødsel fødsel = new Fødsel(forrigeMåned());
        return fødsel;
    }

    private static FramtidigOppholdsInformasjon framtidigOppHoldIUtlandetHeleåret() {
        return new FramtidigOppholdsInformasjon(false, Collections.singletonList(new Utenlandsopphold(CountryCode.SE)));
    }

    private static FramtidigOppholdsInformasjon framtidigOppholdINorge() {
        return new FramtidigOppholdsInformasjon(true, Collections.emptyList());
    }

    private static Søker søker() {
        return søker(false);
    }

    private static Søker søker(boolean isAktør) {
        return new Søker(isAktør ? aktoer() : fnr(), BrukerRolle.MOR);
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

    private static Bruker aktoer() {
        return new AktorId("11111111111111111");
    }

    private static Bruker fnr() {
        return new Fodselsnummer("03016536325");
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
