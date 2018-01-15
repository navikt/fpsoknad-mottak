package no.nav.foreldrepenger.oppslag.domain;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class SerializationTest {

	private static ObjectMapper mapper;

	@BeforeAll
	public static void beforeClass() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.registerModule(new Jdk8Module());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}
	
	@Test
	public void testKjonnSerialization() throws IOException {
		Kjonn deserialized = mapper.readValue(write(Kjonn.K), Kjonn.class);
		assertEquals(Kjonn.K, deserialized);
	}

	@Test
	public void testNameSerialization() throws IOException {
		Name name = name();
		String serialized = write(name);
		Name deserialized = mapper.readValue(serialized, Name.class);
		assertEquals(name, deserialized);
	}

	@Test
	public void testAdresseSerialization() throws IOException {
		Adresse adresse = adresse();
		String serialized = write(adresse);
		Adresse deserialized = mapper.readValue(serialized, Adresse.class);
		assertEquals(adresse, deserialized);
	}

	@Test
	public void testPersonSerialization() throws IOException {
		Person person = new Person(id(), Kjonn.M,name(), birthDate(), adresse());
		String serialized = write(person);
		Person deserialized = mapper.readValue(serialized, Person.class);
		assertEquals(person, deserialized);
	}

	@Test
	public void testFnrSerialization() throws IOException {
		Fodselsnummer fnr = fnr();
		String serialized = write(fnr);
		Fodselsnummer deserialized = mapper.readValue(serialized, Fodselsnummer.class);
		assertEquals(fnr, deserialized);
	}

	@Test
	public void testAktorIdSerialization() throws IOException {
		AktorId aktorid = aktoer();
		String serialized = write(aktorid);
		AktorId deserialized = mapper.readValue(serialized, AktorId.class);
		assertEquals(aktorid, deserialized);
	}

	@Test
	public void testIDPairSerialization() throws IOException {
		ID id = id();
		String serialized = write(id);
		ID deserialized = mapper.readValue(serialized, ID.class);
		assertEquals(id, deserialized);
	}

	private ID id() {
		return new ID(aktoer(), fnr());
	}

	private static Name name() {
		return new Name("Jan-Olav", "Eide");
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
		return new Adresse("NOR", "0360", "Fagerborggata", "6", "A");
	}

	private String write(Object obj) throws JsonProcessingException {
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
	}
}
