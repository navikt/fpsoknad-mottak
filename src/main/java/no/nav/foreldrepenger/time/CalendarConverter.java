package no.nav.foreldrepenger.time;

import java.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public final class CalendarConverter {

	private CalendarConverter() {

	}

	public static LocalDate toDate(XMLGregorianCalendar cal) {
		return LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
	}

	public static XMLGregorianCalendar toCalendar(LocalDate date) {
		try {
			XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar();
			xgc.setYear(date.getYear());
			xgc.setMonth(date.getMonthValue());
			xgc.setDay(date.getDayOfMonth());
			return xgc;
		} catch (DatatypeConfigurationException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static org.joda.time.LocalDate toJodaLocalTime(XMLGregorianCalendar calendar) {
		return org.joda.time.LocalDate.fromCalendarFields(calendar.toGregorianCalendar());
	}

}
