package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.AVSLÅTT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.INNVILGET;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅGÅR;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅ_VENT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK;
import static org.springframework.http.HttpHeaders.LOCATION;

import java.net.URI;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
//import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;

@Component
public class FPFordelResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelResponseHandler.class);
    private final RestTemplate template;
    private final int maxAntallForsøk;

    public FPFordelResponseHandler(RestTemplate template, @Value("${fpfordel.max:5}") int maxAntallForsøk) {
        this.template = template;
        this.maxAntallForsøk = maxAntallForsøk;
    }

    public Kvittering handle(ResponseEntity<FPFordelKvittering> respons, String ref) {
        LOG.info("Behandler respons {}", respons);
        StopWatch timer = new StopWatch();
        timer.start();
        if (!respons.hasBody()) {
            LOG.warn("Fikk ingen kvittering etter leveranse av søknad");
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }

        FPFordelKvittering kvittering = FPFordelKvittering.class.cast(respons.getBody());
        switch (respons.getStatusCode()) {
        case ACCEPTED:
            if (kvittering instanceof FPFordelPendingKvittering) {
                LOG.info("Søknaden er mottatt, men ikke forsøkt behandlet i FPSak");
                FPFordelPendingKvittering pendingKvittering = FPFordelPendingKvittering.class.cast(respons.getBody());
                URI pollURI = locationFra(respons);
                for (int i = 1; i <= maxAntallForsøk; i++) {
                    LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                    splitAndLog(timer);
                    respons = poll(pollURI, "FPFordel", ref, pendingKvittering.getPollInterval().toMillis());
                    kvittering = FPFordelKvittering.class.cast(respons.getBody());
                    LOG.info("Behandler respons {} ({})", respons, i);
                    switch (respons.getStatusCode()) {
                    case OK:
                        if (kvittering instanceof FPFordelPendingKvittering) {
                            LOG.info("Søknaden er mottatt av FPFordel, men ikke forsøkt behandlet i FPSak");
                            pendingKvittering = FPFordelPendingKvittering.class.cast(kvittering);
                            continue;
                        }
                        if (kvittering instanceof FPFordelGosysKvittering) {
                            stopAndLog(timer);
                            return påVentKvittering(ref, FPFordelGosysKvittering.class.cast(kvittering));
                        }
                        LOG.warn("Uventet kvittering {} for statuskode {}", kvittering, respons.getStatusCode());
                        stopAndLog(timer);
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    case SEE_OTHER:
                        FPSakKvittering forsendelsesStatus = pollForsendelsesStatus(locationFra(respons),
                                pendingKvittering.getPollInterval().toMillis(), ref, timer);
                        Kvittering status = forsendelsesStatus != null
                                ? forsendelsesStatusKvittering(ref, forsendelsesStatus)
                                : sendtOgForsøktBehandletKvittering(ref, FPSakFordeltKvittering.class.cast(kvittering));
                        return status;
                    default:
                        LOG.warn("Uventet responskode {} etter leveranse av søknad", respons.getStatusCode());
                        splitAndLog(timer);
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    }
                }
                LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp", maxAntallForsøk);
                return new Kvittering(PÅ_VENT, ref);
            }
        default:
            LOG.warn("Uventet responskode {} ved leveranse av søknad", respons.getStatusCode());
            stopAndLog(timer);
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private static void splitAndLog(StopWatch timer) {
        timer.split();
        LOG.info("Foreløpig medgått tid er {}ms", timer.getSplitTime());
        timer.unsplit();
    }

    private static void stopAndLog(StopWatch timer) {
        timer.stop();
        LOG.info("Medgått tid er {}ms", timer.getTime());
    }

    private static Kvittering forsendelsesStatusKvittering(String ref, FPSakKvittering status) {

        switch (status.getForsendelseStatus()) {
        case AVSLÅTT:
            return kvitteringMedType(AVSLÅTT, ref, status);
        case INNVILGET:
            return kvitteringMedType(INNVILGET, ref, status);
        case PÅ_VENT:
            return kvitteringMedType(PÅ_VENT, ref, status);
        case PÅGÅR:
            return kvitteringMedType(PÅGÅR, ref, status);
        default:
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private static URI locationFra(ResponseEntity<FPFordelKvittering> respons) {
        return Optional
                .ofNullable(respons.getHeaders().getFirst(LOCATION))
                .map(URI::create)
                .orElseThrow(IllegalArgumentException::new);
    }

    private FPSakKvittering pollForsendelsesStatus(URI pollURI, long delayMillis, String ref, StopWatch timer) {
        FPSakKvittering kvittering = null;
        try {
            for (int i = 1; i <= maxAntallForsøk; i++) {
                splitAndLog(timer);
                waitFor(delayMillis);
                LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                kvittering = template.getForEntity(pollURI, FPSakKvittering.class).getBody();
                FPSakStatus status = kvittering.getForsendelseStatus();
                switch (status) {
                case AVSLÅTT:
                case INNVILGET:
                case PÅ_VENT:
                    stopAndLog(timer);
                    LOG.info("Sak {} er {}", kvittering.getSaksnummer(), status.name().toLowerCase());
                    return kvittering;
                case PÅGÅR:
                    splitAndLog(timer);
                    LOG.info("Sak {} pågår fremdeles", kvittering.getSaksnummer());
                }
            }
            stopAndLog(timer);
            return kvittering;
        } catch (Exception e) {
            stopAndLog(timer);
            LOG.warn("Kunne ikke sjekke status for forsendelse på {}", pollURI, e);
            return null;
        }
    }

    private ResponseEntity<FPFordelKvittering> poll(URI uri, String name, String ref, long delayMillis) {
        try {
            waitFor(delayMillis);
            return template.getForEntity(uri, FPFordelKvittering.class);
        } catch (RestClientException | InterruptedException e) {
            LOG.warn("Kunne ikke polle {} på {}", name, uri, e);
            throw new FPFordelUnavailableException(e);
        }
    }

    private static void waitFor(long delayMillis) throws InterruptedException {
        LOG.info("Venter i {}ms", delayMillis);
        Thread.sleep(delayMillis);
    }

    private static Kvittering kvitteringMedType(LeveranseStatus type, String ref, FPSakKvittering status) {
        return kvitteringMedType(type, ref, status.getJournalpostId(), status.getSaksnummer());
    }

    private static Kvittering kvitteringMedType(LeveranseStatus type, String ref, String journalId, String saksnr) {
        Kvittering kvittering = new Kvittering(type, ref);
        kvittering.setJournalId(journalId);
        kvittering.setSaksNr(saksnr);
        return kvittering;
    }

    private static Kvittering påVentKvittering(String ref, FPFordelGosysKvittering gosysKvittering) {
        LOG.info("Søknaden er sendt til manuell behandling i Gosys, journalId er {}",
                gosysKvittering.getJournalpostId());
        return kvitteringMedType(PÅ_VENT, ref, gosysKvittering.getJournalpostId(), null);
    }

    private static Kvittering sendtOgForsøktBehandletKvittering(String ref, FPSakFordeltKvittering kvittering) {
        LOG.info("Søknaden er motatt og forsøkt behandlet av FPSak, journalId er {}, saksnummer er {}",
                kvittering.getJournalpostId(), kvittering.getSaksnummer());
        return kvitteringMedType(SENDT_OG_FORSØKT_BEHANDLET_FPSAK, ref, kvittering.getJournalpostId(),
                kvittering.getSaksnummer());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", maxAntallForsøk=" + maxAntallForsøk + "]";
    }
}
