package no.nav.foreldrepenger.time;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("fast")
public class CalendarConverterTest {

	@Test
	public void toCalendar() throws Exception {
		LocalDate date = LocalDate.of(2017, 12, 13);
		XMLGregorianCalendar expected = DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13");
		XMLGregorianCalendar actual = DateUtil.toXMLGregorianCalendar(date);
		assertEquals(actual, expected);
	}

	@Test
	public void toDate() throws Exception {
		XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13");
		LocalDate expected = LocalDate.of(2017, 12, 13);
		LocalDate actual = DateUtil.toLocalDate(xgc);
		assertEquals(actual, expected);
	}

}
