package no.nav.foreldrepenger.lookup.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.inntekt.Inntekt;
import no.nav.foreldrepenger.lookup.ws.medl.MedlPeriode;
import no.nav.foreldrepenger.lookup.ws.person.*;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.Arbeidsforhold;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Tag("fast")
@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
public class SerializationTest {

    @Inject
    private ObjectMapper mapper;

    @Test
    public void testPostnr() {
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
    public void testBankkontoSerialization() throws IOException {
        test(bankkonto());
    }

    @Test
    public void testKjonnSerialization() throws IOException {
        test(Kjønn.K);
    }

    @Test
    public void testNameSerialization() throws IOException {
        test(name());
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

    @Test
    public void testMedlPeriodeSerialization() throws IOException {
        test(medlPeriode());
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
        return new Person(id(), CountryCode.NO, Kjønn.M, name(), "nynorsk",
            bankkonto(), birthDate(), emptyList());
    }

    private static LocalDate birthDate() {
        return LocalDate.now().minusMonths(2);
    }

    private static Fødselsnummer fnr() {
        return new Fødselsnummer("03016536325");
    }

    private static AktorId aktoer() {
        return new AktorId("11111111111111111");
    }

    private static Bankkonto bankkonto() {
        return new Bankkonto("1234567890", "Pæng r'us");
    }

    private static Ytelse ytelse() {
        return new Ytelse("typen", "statusen", LocalDate.now().minus(Period.ofYears(2)),
                Optional.of(LocalDate.now().minus(Period.ofYears(1))));
    }

    private static Inntekt inntekt() {
        return new Inntekt(LocalDate.now(), Optional.of(LocalDate.now().minusMonths(2)), 1234.5, "acme industries");
    }

    private static MedlPeriode medlPeriode() {
        return new MedlPeriode(LocalDate.now(), Optional.of(LocalDate.now().minusMonths(2)), "statusen", "typen",
                "grunnlagstypen", "landet");
    }

    private static Arbeidsforhold arbeidsforhold() {
        LocalDate now = LocalDate.now();
        return new Arbeidsforhold("arbgiver", "typen", 100d,
                now.minusMonths(2), Optional.of(now));
    }

    private String write(Object obj) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}
