package no.nav.foreldrepenger.lookup.ws.ytelser.sakogbehandling;

import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import no.nav.foreldrepenger.time.DateUtil;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;

import java.util.List;

import static java.util.stream.Collectors.*;

public class SakMapper {

    public static Sak map(no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak sbSak) {
        return new Sak(sbSak.getSaksId(),
            sbSak.getSakstema().getValue(),
            null,
            sbSak.getSaksId(),
            status(sbSak.getBehandlingskjede()),
            DateUtil.toLocalDate(sbSak.getOpprettet()));
    }

    private static String status(List<Behandlingskjede> kjeder) {
        return kjeder.stream()
            .map(b -> b.getSisteBehandlingsstatus().getValue())
            .collect(joining(","));
    }

}
