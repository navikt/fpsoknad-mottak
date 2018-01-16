package no.nav.foreldrepenger.oppslag.infotrygd;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.domain.Benefit;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.InfotrygdSak;

public class InfotrygdsakMapper {

	public static Benefit map(InfotrygdSak sak) {
		return new Benefit(sak.getTema().getTermnavn(), sak.getStatus().getTermnavn(),
		        CalendarConverter.toDate(sak.getVedtatt()), Optional.empty());
	}

}
