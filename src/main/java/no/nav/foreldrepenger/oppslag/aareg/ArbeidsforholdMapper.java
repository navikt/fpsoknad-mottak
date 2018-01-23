package no.nav.foreldrepenger.oppslag.aareg;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;

import java.util.Optional;

public class ArbeidsforholdMapper {

   public static Arbeidsforhold map(no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold forhold) {
      return new Arbeidsforhold(
         new AktorId(forhold.getArbeidsgiver().getAktoerId()),
         forhold.getArbeidsforholdstype().getValue(),
         CalendarConverter.toLocalDate(forhold.getAnsettelsesPeriode().getPeriode().getFom()),
         Optional.ofNullable(forhold.getAnsettelsesPeriode().getPeriode().getTom()).map(CalendarConverter::toLocalDate));
   }

}
