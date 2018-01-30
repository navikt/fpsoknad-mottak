package no.nav.foreldrepenger.oppslag.time;

import java.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public final class CalendarConverter {

    private CalendarConverter() {

    }

    public static LocalDate toLocalDate(XMLGregorianCalendar cal) {
        return LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(LocalDate date) {
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

}
