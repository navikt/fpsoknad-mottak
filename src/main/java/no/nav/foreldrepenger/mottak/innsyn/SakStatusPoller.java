package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.common.domain.Kvittering.forsendelsesStatusKvittering;
import static no.nav.foreldrepenger.common.domain.Kvittering.sendtOgForsøktBehandletKvittering;
import static no.nav.foreldrepenger.common.util.Constants.TOKENX;
import static no.nav.foreldrepenger.mottak.util.TimeUtil.waitFor;

import java.net.URI;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.innsyn.ForsendelsesStatusKvittering;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FPSakFordeltKvittering;

@Service
public class SakStatusPoller extends AbstractRestConnection {

    private static final Logger LOG = LoggerFactory.getLogger(SakStatusPoller.class);

    private final int maxAntallForsøk;

    public SakStatusPoller(@Qualifier(TOKENX) RestOperations restOperations, @Value("${fpinfo.max:10}") int maxAntallForsøk) {
        super(restOperations);
        this.maxAntallForsøk = maxAntallForsøk;
    }

    public Kvittering poll(URI uri, StopWatch timer, Duration delay, FPSakFordeltKvittering fordeltKvittering) {

        var status = poll(uri, delay.toMillis(), timer);
        return status != null
                ? forsendelsesStatusKvittering(status, fordeltKvittering)
                : sendtOgForsøktBehandletKvittering(fordeltKvittering);
    }

    private ForsendelsesStatusKvittering poll(URI pollURI, long delayMillis, StopWatch timer) {
        waitFor(delayMillis);
        LOG.info("Poller forsendelsesstatus på {}", pollURI);
        try {
            for (int i = 1; i <= maxAntallForsøk; i++) {
                LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                var respons = poll(pollURI, delayMillis);
                if (!respons.hasBody()) {
                    LOG.info("Fikk ingen kvittering etter polling av forsendelsesstatus runde {}", i);
                    continue;
                }
                var kvittering = respons.getBody();
                LOG.info("Fikk respons kvittering {}", kvittering);
                switch (kvittering.forsendelseStatus()) {
                    case MOTTATT, AVSLÅTT, INNVILGET, PÅ_VENT -> {
                        stop(timer);
                        LOG.info("Sak har status {} etter {}ms", kvittering.forsendelseStatus().name(),
                                timer.getTime());
                        return kvittering;
                    }
                    case PÅGÅR -> {
                        LOG.info("Prosessering pågår fremdeles etter {}ms", timer.getTime());
                        continue;
                    }
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

    private ResponseEntity<ForsendelsesStatusKvittering> poll(URI pollURI, long delayMillis) {
        return poll(pollURI, delayMillis, ForsendelsesStatusKvittering.class);
    }

    private <T> ResponseEntity<T> poll(URI uri, long delayMillis, Class<T> clazz) {
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
