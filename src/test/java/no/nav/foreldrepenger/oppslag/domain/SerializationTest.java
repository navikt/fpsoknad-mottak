package no.nav.foreldrepenger.oppslag.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.oppslag.person.PoststedFinner;
import no.nav.foreldrepenger.oppslag.person.StatiskPoststedFinner;

@Tag("fast")
public class SerializationTest {

    private static ObjectMapper mapper;

    @BeforeAll
    public static void beforeClass() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    @Test
    public void testPostnr() throws IOException {
        PoststedFinner finner = new StatiskPoststedFinner();
        assertTrue(finner.poststed("1353").equalsIgnoreCase("Bærums Verk"));
        assertTrue(finner.poststed("1332").equalsIgnoreCase("Østerås"));

    }

    @Test
    public void testYtelseSerialization() throws IOException {
        test(ytelse());
    }

    @Test
    public void testArbeidsforholdSerialization() throws IOException {
        test(arbeidsforhold());
    }

    @Test
    public void testKjonnSerialization() throws IOException {
        test(Kjonn.K);
    }

    @Test
    public void testNameSerialization() throws IOException {
        test(name());
    }

    @Test
    public void testAdresseSerialization() throws IOException {
        test(adresse());
    }

    @Test
    public void testPersonSerialization() throws IOException {
        test(person());
    }

    @Test
    public void testFnrSerialization() throws IOException {
        test(fnr());
    }

    @Test
    public void testAktorIdSerialization() throws IOException {
        test(aktoer());
    }

    @Test
    public void testIDPairSerialization() throws IOException {
        test(id());
    }

   @Test
   public void testInntektSerialization() throws IOException {
      test(inntekt());
   }

    private void test(Object object) throws IOException {
        String serialized = write(object);
        Object deserialized = mapper.readValue(serialized, object.getClass());
        assertEquals(object, deserialized);
    }

    private static ID id() {
        return new ID(aktoer(), fnr());
    }

    private static Navn name() {
        return new Navn("Jan-Olav", "Kjørås", "Eide");
    }

    private static Person person() {
        return new Person(id(), CountryCode.NO, Kjonn.M, name(), adresse(), birthDate(), Collections.emptyList());
    }

    private static LocalDate birthDate() {
        return LocalDate.now().minusMonths(2);
    }

    private static Fodselsnummer fnr() {
        return new Fodselsnummer("03016536325");
    }

    private static AktorId aktoer() {
        return new AktorId("11111111111111111");
    }

    private static Adresse adresse() {
        return new Adresse(CountryCode.NO, "0360", "Oslo", "Fagerborggata", "6", "A");
    }

    private static Ytelse ytelse() {
        return new Ytelse("typen", "statusen", LocalDate.now().minus(Period.ofYears(2)),
                Optional.of(LocalDate.now().minus(Period.ofYears(1))));
    }

   private static Inntekt inntekt() {
      return new Inntekt(LocalDate.now(), Optional.of(LocalDate.now().minusMonths(2)), 1234.5, "acme industries");
   }

    private static Arbeidsforhold arbeidsforhold() {
        LocalDate now = LocalDate.now();
        return new Arbeidsforhold("arbgiver", "typen", "yrket",
                now.minusMonths(2), Optional.of(now));
    }

    private String write(Object obj) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}
