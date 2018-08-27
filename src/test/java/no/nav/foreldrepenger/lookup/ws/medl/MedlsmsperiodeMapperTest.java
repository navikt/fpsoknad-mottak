package no.nav.foreldrepenger.lookup.ws.medl;

import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.Medlemsperiode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MedlsmsperiodeMapperTest {

   @Test
   @Tag("fast")
   public void mapValues() {
      LocalDate now = LocalDate.now();
      LocalDate earlier = now.minusMonths(2);
       Medlemsperiode periode = TestdataProvider.medlemsperiode(earlier, now);
      MedlPeriode expected = new MedlPeriode(
         earlier,
         Optional.of(now),
         "statusen",
         "typen",
         "grunnlaget",
         "landet");
      MedlPeriode actual = MedlemsperiodeMapper.map(periode);
      assertEquals(expected, actual);
   }

}
