package no.nav.foreldrepenger.lookup.ws.ytelser.fpsak;

import java.util.List;

import no.nav.foreldrepenger.lookup.Pingable;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;

public interface FpsakClient extends Pingable {

    List<Ytelse> casesFor(AktorId aktor);

}
