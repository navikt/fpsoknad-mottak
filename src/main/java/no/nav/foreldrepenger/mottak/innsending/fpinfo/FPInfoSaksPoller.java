package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.AVSLÅTT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.INNVILGET;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅGÅR;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅ_VENT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK;

import java.net.URI;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.http.errorhandling.RemoteUnavailableException;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPSakFordeltKvittering;

//@Service
public class FPInfoSaksPoller implements SaksStatusPoller {

    private static final Logger LOG = LoggerFactory.getLogger(FPInfoSaksPoller.class);

    private final RestTemplate template;
    private final int maxAntallForsøk;

    public FPInfoSaksPoller(RestTemplate template, @Value("${fpfordel.max:5}") int maxAntallForsøk) {
        this.template = template;
        this.maxAntallForsøk = maxAntallForsøk;
    }

    @Override
    public Kvittering poll(URI uri, String ref, StopWatch timer, Duration delay,
            FPSakFordeltKvittering fordeltKvittering) {
        FPInfoKvittering forsendelsesStatus = pollForsendelsesStatus(uri, delay.toMillis(), ref, timer);
        return forsendelsesStatus != null
                ? forsendelsesStatusKvittering(forsendelsesStatus, fordeltKvittering, ref)
                : sendtOgForsøktBehandletKvittering(ref, fordeltKvittering);
    }

    private FPInfoKvittering pollForsendelsesStatus(URI pollURI, long delayMillis, String ref, StopWatch timer) {
        FPInfoKvittering kvittering = null;

        LOG.info("Poller forsendelsesstatus på {}", pollURI);
        try {
            for (int i = 1; i <= maxAntallForsøk; i++) {
                LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                ResponseEntity<FPInfoKvittering> respons = pollFPInfo(pollURI, delayMillis);
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

    private ResponseEntity<FPInfoKvittering> pollFPInfo(URI pollURI, long delayMillis) {
        return poll(pollURI, "FPInfo", delayMillis, FPInfoKvittering.class);
    }

    private <T> ResponseEntity<T> poll(URI uri, String name, long delayMillis, Class<T> clazz) {
        try {
            waitFor(delayMillis);
            return template.getForEntity(uri, clazz);
        } catch (RestClientException | InterruptedException e) {
            LOG.warn("Kunne ikke polle {} på {}", name, uri, e);
            throw new RemoteUnavailableException(uri, e);
        }
    }

    private static void waitFor(long delayMillis) throws InterruptedException {
        LOG.trace("Venter i {}ms", delayMillis);
        Thread.sleep(delayMillis);
    }

    private static long stop(StopWatch timer) {
        timer.stop();
        return timer.getTime();
    }

    private static Kvittering forsendelsesStatusKvittering(FPInfoKvittering forsendelsesStatus,
            FPSakFordeltKvittering fordeltKvittering, String ref) {

        switch (forsendelsesStatus.getForsendelseStatus()) {
        case AVSLÅTT:
            return kvitteringMedType(AVSLÅTT, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        case INNVILGET:
            return kvitteringMedType(INNVILGET, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        case PÅ_VENT:
            return kvitteringMedType(PÅ_VENT, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        case PÅGÅR:
            return kvitteringMedType(PÅGÅR, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        default:
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private static Kvittering sendtOgForsøktBehandletKvittering(String ref, FPSakFordeltKvittering kvittering) {
        LOG.info("Søknaden er motatt og forsøkt behandlet av FPSak, journalId er {}, saksnummer er {}",
                kvittering.getJournalpostId(), kvittering.getSaksnummer());
        return kvitteringMedType(SENDT_OG_FORSØKT_BEHANDLET_FPSAK, ref, kvittering.getJournalpostId(),
                kvittering.getSaksnummer());
    }

    private static Kvittering kvitteringMedType(LeveranseStatus type, String ref, String journalId, String saksnr) {
        Kvittering kvittering = new Kvittering(type, ref);
        kvittering.setJournalId(journalId);
        kvittering.setSaksNr(saksnr);
        return kvittering;
    }

}
