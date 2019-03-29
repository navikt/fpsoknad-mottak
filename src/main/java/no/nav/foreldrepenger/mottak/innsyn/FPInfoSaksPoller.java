package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.domain.Kvittering.forsendelsesStatusKvittering;
import static no.nav.foreldrepenger.mottak.domain.Kvittering.sendtOgForsøktBehandletKvittering;
import static no.nav.foreldrepenger.mottak.util.TimeUtil.waitFor;

import java.net.URI;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPSakFordeltKvittering;

@Service
public class FPInfoSaksPoller extends AbstractRestConnection {

    private static final Logger LOG = LoggerFactory.getLogger(FPInfoSaksPoller.class);

    private final int maxAntallForsøk;

    public FPInfoSaksPoller(RestOperations RestOperations, @Value("${fpinfo.max:10}") int maxAntallForsøk) {
        super(RestOperations);
        this.maxAntallForsøk = maxAntallForsøk;
    }

    public Kvittering poll(URI uri, StopWatch timer, Duration delay,
            FPSakFordeltKvittering fordeltKvittering) {

        ForsendelsesStatusKvittering forsendelsesStatus = pollForsendelsesStatus(uri, delay.toMillis(), timer);
        return forsendelsesStatus != null
                ? forsendelsesStatusKvittering(forsendelsesStatus, fordeltKvittering)
                : sendtOgForsøktBehandletKvittering(fordeltKvittering);
    }

    private ForsendelsesStatusKvittering pollForsendelsesStatus(URI pollURI, long delayMillis,
            StopWatch timer) {
        waitFor(delayMillis);
        LOG.info("Poller forsendelsesstatus på {}", pollURI);
        try {
            for (int i = 1; i <= maxAntallForsøk; i++) {
                LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                ResponseEntity<ForsendelsesStatusKvittering> respons = pollFPInfo(pollURI, delayMillis);
                if (!respons.hasBody()) {
                    LOG.warn("Fikk ingen kvittering etter polling av forsendelsesstatus");
                    return null;
                }
                ForsendelsesStatusKvittering kvittering = respons.getBody();
                LOG.info("Fikk respons kvittering {}", kvittering);
                switch (kvittering.getForsendelseStatus()) {
                case MOTTATT:
                case AVSLÅTT:
                case INNVILGET:
                case PÅ_VENT:
                    stop(timer);
                    LOG.info("Sak har status {} etter {}ms", kvittering.getForsendelseStatus().name(),
                            timer.getTime());
                    return kvittering;
                case PÅGÅR:
                    LOG.info("Prosessering pågår fremdeles etter {}ms", timer.getTime());
                    continue;
                }
            }
            stop(timer);
            return ForsendelsesStatusKvittering.PÅGÅR;
        } catch (Exception e) {
            stop(timer);
            LOG.warn("Kunne ikke sjekke status for forsendelse på {}", pollURI, e);
            return null;
        }
    }

    private ResponseEntity<ForsendelsesStatusKvittering> pollFPInfo(URI pollURI, long delayMillis) {
        return poll(pollURI, "FPInfo", delayMillis, ForsendelsesStatusKvittering.class);
    }

    private <T> ResponseEntity<T> poll(URI uri, String name, long delayMillis, Class<T> clazz) {
        waitFor(delayMillis);
        return getForEntity(uri, clazz);
    }

    private static long stop(StopWatch timer) {
        timer.stop();
        return timer.getTime();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [maxAntallForsøk=" + maxAntallForsøk + "]";
    }
}
