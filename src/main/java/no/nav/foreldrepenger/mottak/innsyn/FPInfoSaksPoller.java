package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.AVSLÅTT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.INNVILGET;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅGÅR;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅ_VENT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK;
import static no.nav.foreldrepenger.mottak.util.TimeUtil.waitFor;

import java.net.URI;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPSakFordeltKvittering;

@Service
public class FPInfoSaksPoller extends AbstractRestConnection implements SaksStatusPoller {

    private static final Counter PENDING = Metrics.counter("fpinfo.kvitteringer.påvent");
    private static final Counter REJECTED = Metrics.counter("fpinfo.kvitteringer.avslått");
    private static final Counter ACCEPTED = Metrics.counter("fpinfo.kvitteringer.innvilget");
    private static final Counter RUNNING = Metrics.counter("fpinfo.kvitteringer.pågår");
    private static final Counter FAILED = Metrics.counter("fpinfo.kvitteringer.feilet");

    private static final Logger LOG = LoggerFactory.getLogger(FPInfoSaksPoller.class);

    private final int maxAntallForsøk;

    public FPInfoSaksPoller(RestTemplate template, @Value("${fpfordel.max:5}") int maxAntallForsøk) {
        super(template);
        this.maxAntallForsøk = maxAntallForsøk;
    }

    @Override
    public Kvittering poll(URI uri, String ref, StopWatch timer, Duration delay,
            FPSakFordeltKvittering fordeltKvittering) {
        ForsendelsesStatusKvittering forsendelsesStatus = pollForsendelsesStatus(uri, delay.toMillis(), ref, timer);
        return forsendelsesStatus != null
                ? forsendelsesStatusKvittering(forsendelsesStatus, fordeltKvittering, ref)
                : sendtOgForsøktBehandletKvittering(ref, fordeltKvittering);
    }

    private ForsendelsesStatusKvittering pollForsendelsesStatus(URI pollURI, long delayMillis, String ref,
            StopWatch timer) {
        ForsendelsesStatusKvittering kvittering = null;

        LOG.info("Poller forsendelsesstatus på {}", pollURI);
        try {
            for (int i = 1; i <= maxAntallForsøk; i++) {
                LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                ResponseEntity<ForsendelsesStatusKvittering> respons = pollFPInfo(pollURI, delayMillis);
                if (!respons.hasBody()) {
                    LOG.warn("Fikk ingen kvittering etter polling av forsendelsesstatus");
                    return null;
                }
                LOG.info("Fikk respons status kode {}", respons.getStatusCode());
                kvittering = respons.getBody();
                LOG.info("Fikk respons kvittering {}", kvittering);
                switch (kvittering.getForsendelseStatus()) {
                case AVSLÅTT:
                case INNVILGET:
                case PÅ_VENT:
                    stop(timer);
                    LOG.info("Sak har status {} etter {}ms", kvittering.getForsendelseStatus().name(),
                            timer.getTime());
                    return kvittering;
                case PÅGÅR:
                    LOG.info("Sak pågår fremdeles etter {}ms", timer.getTime());
                    continue;
                default:
                    LOG.info("Dette skal ikke skje");
                }
            }
            stop(timer);
            return kvittering;
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

    private static Kvittering forsendelsesStatusKvittering(ForsendelsesStatusKvittering forsendelsesStatus,
            FPSakFordeltKvittering fordeltKvittering, String ref) {

        switch (forsendelsesStatus.getForsendelseStatus()) {
        case AVSLÅTT:
            REJECTED.increment();
            return kvitteringMedType(AVSLÅTT, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        case INNVILGET:
            ACCEPTED.increment();
            return kvitteringMedType(INNVILGET, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        case PÅ_VENT:
            PENDING.increment();
            return kvitteringMedType(PÅ_VENT, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        case PÅGÅR:
            RUNNING.increment();
            return kvitteringMedType(PÅGÅR, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        default:
            FAILED.increment();
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private static Kvittering sendtOgForsøktBehandletKvittering(String ref, FPSakFordeltKvittering kvittering) {
        LOG.info("Søknaden er motatt og forsøkt behandlet av FPSak, journalId er {}, saksnummer er {}",
                kvittering.getJournalpostId(), kvittering.getSaksnummer());
        FAILED.increment();
        return kvitteringMedType(SENDT_OG_FORSØKT_BEHANDLET_FPSAK, ref, kvittering.getJournalpostId(),
                kvittering.getSaksnummer());
    }

    private static Kvittering kvitteringMedType(LeveranseStatus type, String ref, String journalId, String saksnr) {
        Kvittering kvittering = new Kvittering(type, ref);
        kvittering.setJournalId(journalId);
        kvittering.setSaksNr(saksnr);
        return kvittering;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
