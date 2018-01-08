package no.nav.foreldrepenger.oppslag.time;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;

import javax.xml.datatype.*;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.oppslag.time.CalendarConverter;

public class CalendarConverterTest {

	@Test
	public void toCalendar() throws Exception {
		LocalDate date = LocalDate.of(2017, 12, 13);
		XMLGregorianCalendar expected = DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13");
		XMLGregorianCalendar actual = CalendarConverter.toCalendar(date);
		assertEquals(actual, expected);
	}

	@Test
	public void toDate() throws Exception {
		XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13");
		LocalDate expected = LocalDate.of(2017, 12, 13);
		LocalDate actual = CalendarConverter.toDate(xgc);
		assertEquals(actual, expected);
	}

}
