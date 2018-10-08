package no.nav.foreldrepenger.lookup.ws.ytelser.sakogbehandling;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.*;

public class SakOgBehandlingClientWs implements SakOgBehandlingClient {

    private final SakOgBehandlingV1 sakOgBehandlingV1;
    private final SakOgBehandlingV1 healthIndicator;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.sakogbehandling");
    private static final Logger LOG = LoggerFactory.getLogger(SakOgBehandlingClientWs.class);

    @Inject
    public SakOgBehandlingClientWs(SakOgBehandlingV1 sakOgBehandlingV1, SakOgBehandlingV1 healthIdicator) {
        this.sakOgBehandlingV1 = sakOgBehandlingV1;
        this.healthIndicator = healthIdicator;
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger SakOgBehandling");
            healthIndicator.ping();
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    @Override
    public List<Sak> casesFor(AktorId aktorId) {
        FinnSakOgBehandlingskjedeListeRequest req = new FinnSakOgBehandlingskjedeListeRequest();
        req.setAktoerREF(aktorId.getAktør());
        req.setKunAapneBehandlingskjeder(true);
        FinnSakOgBehandlingskjedeListeResponse response = sakOgBehandlingV1.finnSakOgBehandlingskjedeListe(req);
        return response.getSak().stream()
            .map(SakMapper::map)
            .collect(toList());
    }
}
