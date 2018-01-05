package no.nav.foreldrepenger.selvbetjening.domain;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class SerializationTest {

	private static ObjectMapper mapper;

	@BeforeClass
	public static void beforeClass() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JodaModule());
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
		Person person = new Person(ids(), name(), birthDate(), adresse());
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
		ID ids = new ID(aktoer(), fnr());
		String serialized = write(ids);
		ID deserialized = mapper.readValue(serialized, ID.class);
		assertEquals(ids, deserialized);
	}

	private ID ids() {
		return new ID(aktoer(), fnr());
	}

	private static Name name() {
		return new Name("Jan-Olav", "Eide");
	}

	private static LocalDate birthDate() {
		return DateTime.now().minusMonths(2).toLocalDate();
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
		String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		System.out.println(serialized);
		return serialized;
	}
}
