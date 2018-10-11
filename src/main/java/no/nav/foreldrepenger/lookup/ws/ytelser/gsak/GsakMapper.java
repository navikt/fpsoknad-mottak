package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import no.nav.foreldrepenger.time.DateUtil;
import no.nav.tjeneste.virksomhet.sak.v2.WSSak;

class GsakMapper {

    static Sak map(WSSak sak) {
        return new Sak(sak.getSakId(), sak.getSakstype(), sak.getFagsystem(),
            sak.getFagsystemSakId(), "", DateUtil.toLocalDate(sak.getOpprettelsetidspunkt()));
    }

}
