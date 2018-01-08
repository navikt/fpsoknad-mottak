package no.nav.foreldrepenger.inntekt;

import no.nav.foreldrepenger.domain.Income;
import no.nav.foreldrepenger.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt;

class InntektMapper {

	public static Income map(Inntekt inntekt) {
		return new Income(CalendarConverter.toDate(inntekt.getOpptjeningsperiode().getStartDato()),
		        CalendarConverter.toDate(inntekt.getOpptjeningsperiode().getSluttDato()),
		        inntekt.getBeloep().doubleValue());
	}

}
