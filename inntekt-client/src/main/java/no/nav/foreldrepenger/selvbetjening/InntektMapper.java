package no.nav.foreldrepenger.selvbetjening;

import no.nav.foreldrepenger.selvbetjening.inntekt.domain.Income;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt;

class InntektMapper {

   public static Income map(Inntekt inntekt) {
      return new Income(
         CalendarConverter.toDate(inntekt.getOpptjeningsperiode().getStartDato()),
         CalendarConverter.toDate(inntekt.getOpptjeningsperiode().getSluttDato()),
         inntekt.getBeloep().doubleValue());
   }

}
