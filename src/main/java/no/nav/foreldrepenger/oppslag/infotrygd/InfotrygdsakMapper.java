package no.nav.foreldrepenger.oppslag.infotrygd;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.InfotrygdSak;

public class InfotrygdsakMapper {

	public static Ytelse map(InfotrygdSak sak) {
		return new Ytelse(sak.getTema().getTermnavn(), sak.getStatus().getTermnavn(),
		        CalendarConverter.toDate(sak.getVedtatt()), Optional.empty(), "Infotrygd");
	}

}
