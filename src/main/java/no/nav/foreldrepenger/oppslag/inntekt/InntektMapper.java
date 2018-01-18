package no.nav.foreldrepenger.oppslag.inntekt;

import no.nav.foreldrepenger.oppslag.domain.Inntekt;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;

final class InntektMapper {
	
	private InntektMapper() {
		
	}

	public static Inntekt map(no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt inntekt) {
		return new Inntekt(CalendarConverter.toLocalDate(inntekt.getOpptjeningsperiode().getStartDato()),
		        CalendarConverter.toLocalDate(inntekt.getOpptjeningsperiode().getSluttDato()),
		        inntekt.getBeloep().doubleValue());
	}

}
