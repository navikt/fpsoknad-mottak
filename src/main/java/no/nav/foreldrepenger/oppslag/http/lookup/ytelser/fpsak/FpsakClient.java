package no.nav.foreldrepenger.oppslag.http.lookup.ytelser.fpsak;

import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.http.lookup.aktor.AktorId;

import java.util.List;

public interface FpsakClient {

    void ping();

    List<Ytelse> casesFor(AktorId aktor);

}
