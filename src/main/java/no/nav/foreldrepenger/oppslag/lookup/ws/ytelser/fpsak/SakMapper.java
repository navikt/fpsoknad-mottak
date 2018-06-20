package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.fpsak;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.time.DateUtil;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Sak;

public class SakMapper {

    public static Ytelse map(Sak sak) {
        return new Ytelse(sak.getBehandlingstema().getTermnavn(), sak.getStatus().getTermnavn(),
                DateUtil.toLocalDate(sak.getOpprettet()), Optional.empty());
    }

}
