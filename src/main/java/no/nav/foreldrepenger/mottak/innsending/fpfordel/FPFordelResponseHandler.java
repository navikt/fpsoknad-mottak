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

    public Kvittering handle(ResponseEntity<FPFordelKvittering> leveranseRespons, String ref) {
        LOG.info("Behandler respons {}", leveranseRespons);
        StopWatch timer = new StopWatch();
        timer.start();
        if (!leveranseRespons.hasBody()) {
            LOG.warn("Fikk ingen kvittering etter leveranse av søknad");
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }

        FPFordelKvittering kvittering = FPFordelKvittering.class.cast(leveranseRespons.getBody());

        switch (leveranseRespons.getStatusCode()) {
        case ACCEPTED:
            if (kvittering instanceof FPFordelPendingKvittering) {
                LOG.info("Søknaden er mottatt, men ikke forsøkt behandlet i FPSak");
                FPFordelPendingKvittering pending = FPFordelPendingKvittering.class.cast(leveranseRespons.getBody());
                URI pollURI = locationFra(leveranseRespons);
                for (int i = 1; i <= maxAntallForsøk; i++) {
                    LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                    ResponseEntity<FPFordelKvittering> pollRespons = pollFPFordel(pollURI, "FPFordel", ref,
                            pending.getPollInterval().toMillis());
                    kvittering = FPFordelKvittering.class.cast(pollRespons.getBody());
                    LOG.info("Behandler poll respons {} etter {}ms", pollRespons, timer.getTime());
                    switch (pollRespons.getStatusCode()) {
                    case OK:
                        if (kvittering instanceof FPFordelPendingKvittering) {
                            LOG.info("Fikk pending kvittering  på {}. forsøk", i);
                            pending = FPFordelPendingKvittering.class.cast(kvittering);
                            continue;
                        }
                        if (kvittering instanceof FPFordelGosysKvittering) {
                            LOG.info("Fikk Gosys kvittering  på {}. forsøk, returnerer etter {}ms", i, stop(timer));
                            return påVentKvittering(ref, FPFordelGosysKvittering.class.cast(kvittering));
                        }
                        LOG.warn("Uventet kvittering {} for statuskode {}, gir opp (etter {}ms)", kvittering,
                                pollRespons.getStatusCode(), stop(timer));
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    case SEE_OTHER:
                        return pollFPInfo(pollRespons, ref, timer, kvittering,
                                pending.getPollInterval().toMillis());
                    default:
                        LOG.warn("Uventet responskode {} etter leveranse av søknad, gir opp (etter {}ms)",
                                pollRespons.getStatusCode(),
                                timer.getTime());
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    }
                }
                LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp (etter {}ms)", maxAntallForsøk,
                        stop(timer));
                return new Kvittering(PÅ_VENT, ref);
            }
        default:
            LOG.warn("Uventet responskode {} ved leveranse av søknad, gir opp (etter {}ms)",
                    leveranseRespons.getStatusCode(),
                    stop(timer));
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private Kvittering pollFPInfo(ResponseEntity<FPFordelKvittering> respons, String ref, StopWatch timer,
            FPFordelKvittering kvittering, long delayMillis) {
        FPSakKvittering forsendelsesStatus = pollForsendelsesStatus(locationFra(respons), delayMillis, ref, timer);
        return forsendelsesStatus != null
                ? forsendelsesStatusKvittering(forsendelsesStatus, ref)
                : sendtOgForsøktBehandletKvittering(ref, FPSakFordeltKvittering.class.cast(kvittering));
    }

    private static long stop(StopWatch timer) {
        timer.stop();
        return timer.getTime();
    }

    private static Kvittering forsendelsesStatusKvittering(FPSakKvittering status, String ref) {

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
                waitFor(delayMillis);
                LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                kvittering = template.getForEntity(pollURI, FPSakKvittering.class).getBody();
                FPSakStatus status = kvittering.getForsendelseStatus();
                switch (status) {
                case AVSLÅTT:
                case INNVILGET:
                case PÅ_VENT:
                    stop(timer);
                    LOG.info("Sak {} {} etter {}ms", kvittering.getSaksnummer(), status.name().toLowerCase(),
                            timer.getTime());
                    return kvittering;
                case PÅGÅR:
                    LOG.info("Sak {} pågår fremdeles etter {}ms", kvittering.getSaksnummer(), timer.getTime());
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

    private ResponseEntity<FPFordelKvittering> pollFPFordel(URI uri, String name, String ref, long delayMillis) {
        try {
            waitFor(delayMillis);
            return template.getForEntity(uri, FPFordelKvittering.class);
        } catch (RestClientException | InterruptedException e) {
            LOG.warn("Kunne ikke polle {} på {}", name, uri, e);
            throw new FPFordelUnavailableException(e);
        }
    }

    private static void waitFor(long delayMillis) throws InterruptedException {
        LOG.trace("Venter i {}ms", delayMillis);
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
