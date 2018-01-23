package no.nav.foreldrepenger.oppslag.inntekt;

import no.nav.foreldrepenger.oppslag.domain.Inntekt;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Periode;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InntektMapperTest {

   @Test
   public void allValuesSet() throws Exception {
      no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt inntekt = inntekt();
      Inntekt expected = new Inntekt(1234.5, LocalDate.of(2017, 12, 13),
         Optional.of(LocalDate.of(2017, 12, 14)));
      Inntekt actual = InntektMapper.map(inntekt);
      assertEquals(expected, actual);
   }

   private no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt inntekt() throws Exception {
      no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt inntekt =
         new no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt();
      Periode periode = new Periode();
      periode.setStartDato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13"));
      periode.setSluttDato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-14"));
      inntekt.setOpptjeningsperiode(periode);
      inntekt.setBeloep(BigDecimal.valueOf(1234.5));
      return inntekt;
   }

}
