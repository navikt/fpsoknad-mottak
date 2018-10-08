package no.nav.foreldrepenger.lookup.ws.ytelser.sakogbehandling;

import no.nav.foreldrepenger.lookup.Pingable;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;

import java.util.List;

public interface SakOgBehandlingClient extends Pingable {

    List<Sak> casesFor(AktorId aktorId);
}
