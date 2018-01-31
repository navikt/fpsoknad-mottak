package no.nav.foreldrepenger.oppslag.inntekt;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.domain.Inntekt;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Aktoer;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.AktoerId;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Organisasjon;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.PersonIdent;

final class InntektMapper {

   public static Inntekt map(no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt inntekt) {
      return new Inntekt(
         Optional.ofNullable(inntekt.getOpptjeningsperiode())
            .map(p -> CalendarConverter.toLocalDate(p.getStartDato())).orElse(null),
         Optional.ofNullable(inntekt.getOpptjeningsperiode())
            .map(p -> CalendarConverter.toLocalDate(p.getSluttDato())),
         Optional.ofNullable(inntekt.getBeloep()).map(b -> b.doubleValue()).orElse(0.0),
         employerID(inntekt.getVirksomhet())
      );
   }

   private static String employerID(Aktoer aktoer) {
      if (aktoer instanceof PersonIdent) {
         return ((PersonIdent) aktoer).getPersonIdent();
      } else if (aktoer instanceof Organisasjon) {
         return ((Organisasjon) aktoer).getOrgnummer();
      } else {
         return ((AktoerId) aktoer).getAktoerId();
      }
   }

}
