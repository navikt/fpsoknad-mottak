package no.nav.foreldrepenger.lookup.ws.ytelser.fpsak;

import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;

import java.util.List;

public interface FpsakClient {

    void ping();

    List<Ytelse> casesFor(AktorId aktor);

}
