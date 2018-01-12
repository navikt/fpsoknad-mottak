package no.nav.foreldrepenger.oppslag.arena;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt;

public class YtelseskontraktMapper {

   public static Ytelse map(Ytelseskontrakt kontrakt) {
      return new Ytelse(kontrakt.getYtelsestype(),
         kontrakt.getStatus(),
         CalendarConverter.toDate(kontrakt.getFomGyldighetsperiode()),
         Optional.ofNullable(kontrakt.getTomGyldighetsperiode())
            .map(CalendarConverter::toDate),
         "Arena");
   }

}
