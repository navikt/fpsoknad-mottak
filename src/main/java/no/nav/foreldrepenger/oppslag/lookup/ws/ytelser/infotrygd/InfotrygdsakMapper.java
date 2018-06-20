package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.infotrygd;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.time.DateUtil;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.InfotrygdSak;

class InfotrygdsakMapper {

    private InfotrygdsakMapper() {

    }

    static Ytelse map(InfotrygdSak sak) {
        return new Ytelse(sak.getTema().getTermnavn(), sak.getStatus().getTermnavn(),
                DateUtil.toLocalDate(sak.getVedtatt()), Optional.empty());
    }

}
