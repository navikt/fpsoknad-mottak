package no.nav.foreldrepenger.selvbetjening;

import java.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class CalendarConverter {

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

}
