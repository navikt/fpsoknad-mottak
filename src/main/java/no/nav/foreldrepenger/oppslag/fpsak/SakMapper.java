package no.nav.foreldrepenger.oppslag.fpsak;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Sak;

public class SakMapper {

	public static Ytelse map(Sak sak) {
		return new Ytelse(sak.getBehandlingstema().getTermnavn(), sak.getStatus().getTermnavn(),
		        CalendarConverter.toDate(sak.getOpprettet()), Optional.empty(), "FPSAK");
	}

}
