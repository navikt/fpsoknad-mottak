package no.nav.foreldrepenger.lookup.ws.ytelser.arena;

import java.util.Optional;

import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.time.DateUtil;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt;

class YtelseskontraktMapper {

    private YtelseskontraktMapper() {

    }

    static Ytelse map(Ytelseskontrakt kontrakt) {
        return new Ytelse(kontrakt.getYtelsestype(), kontrakt.getStatus(),
                DateUtil.toLocalDate(kontrakt.getFomGyldighetsperiode()),
                Optional.ofNullable(kontrakt.getTomGyldighetsperiode()).map(DateUtil::toLocalDate));
    }

}
