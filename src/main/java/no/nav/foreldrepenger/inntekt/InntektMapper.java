package no.nav.foreldrepenger.inntekt;

import no.nav.foreldrepenger.domain.*;
import no.nav.foreldrepenger.time.*;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.*;

class InntektMapper {

	public static Income map(Inntekt inntekt) {
		return new Income(CalendarConverter.toDate(inntekt.getOpptjeningsperiode().getStartDato()),
		        CalendarConverter.toDate(inntekt.getOpptjeningsperiode().getSluttDato()),
		        inntekt.getBeloep().doubleValue());
	}

}
