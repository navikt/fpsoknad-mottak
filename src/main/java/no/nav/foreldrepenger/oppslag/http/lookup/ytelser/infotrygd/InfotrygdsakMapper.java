package no.nav.foreldrepenger.oppslag.http.lookup.ytelser.infotrygd;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.InfotrygdSak;

class InfotrygdsakMapper {

    private InfotrygdsakMapper() {

    }

    static Ytelse map(InfotrygdSak sak) {
        return new Ytelse(sak.getTema().getTermnavn(), sak.getStatus().getTermnavn(),
                CalendarConverter.toLocalDate(sak.getVedtatt()), Optional.empty());
    }

}
