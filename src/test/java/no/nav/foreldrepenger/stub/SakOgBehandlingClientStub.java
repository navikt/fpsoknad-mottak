package no.nav.foreldrepenger.stub;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import no.nav.foreldrepenger.lookup.ws.ytelser.sakogbehandling.SakOgBehandlingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class SakOgBehandlingClientStub implements SakOgBehandlingClient {

    private static final Logger LOG = LoggerFactory.getLogger(SakOgBehandlingClientStub.class);

    @Override
    public List<Sak> casesFor(AktorId aktor) {
        return Arrays.asList(
            new Sak("sak1", "typen", "statusen", "fagomr", "systemet",
                "fsid1", LocalDate.of(2018,9,19)),
            new Sak("sak2", "typen", "statusen", "fagomr", "systemet",
                "fsid2", LocalDate.of(2018,9,18)),
            new Sak("sak3", "typen", "statusen", "fagomr", "systemet",
                "fsid3", LocalDate.of(2018,9,17))
        );
    }

    @Override
    public void ping() {
        LOG.debug("PONG");
    }
}
