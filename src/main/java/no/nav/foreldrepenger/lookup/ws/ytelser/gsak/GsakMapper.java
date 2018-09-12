package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.time.DateUtil;
import no.nav.tjeneste.virksomhet.sak.v2.WSSak;

import java.util.Optional;

class GsakMapper {

    static Ytelse map(WSSak sak) {
        return new Ytelse(sak.getFagomrade() + "/" + sak.getSakstype(), "ukjent",
                DateUtil.toLocalDate(sak.getOpprettelsetidspunkt()), Optional.empty());
    }

}
