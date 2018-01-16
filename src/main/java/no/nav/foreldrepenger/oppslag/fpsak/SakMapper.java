package no.nav.foreldrepenger.oppslag.fpsak;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.domain.Benefit;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Sak;

public class SakMapper {

	public static Benefit map(Sak sak) {
		return new Benefit(sak.getBehandlingstema().getTermnavn(), sak.getStatus().getTermnavn(),
		        CalendarConverter.toDate(sak.getOpprettet()), Optional.empty());
	}

}
