package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.GOSYS;
import static org.springframework.http.HttpHeaders.LOCATION;

import java.net.URI;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.http.errorhandling.RemoteUnavailableException;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.SaksStatusPoller;

@Component
public class FPFordelResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelResponseHandler.class);
    private final RestTemplate template;
    private final int maxAntallForsøk;
    private final SaksStatusPoller poller;

    public FPFordelResponseHandler(RestTemplate template, @Value("${fpfordel.max:5}") int maxAntallForsøk,
            SaksStatusPoller poller) {
        this.template = template;
        this.maxAntallForsøk = maxAntallForsøk;
        this.poller = poller;
    }

    public Kvittering handle(ResponseEntity<FPFordelKvittering> leveranseRespons, String ref) {
        LOG.info("Behandler respons {}", leveranseRespons);
        StopWatch timer = new StopWatch();
        timer.start();
        if (!leveranseRespons.hasBody()) {
            LOG.warn("Fikk ingen kvittering etter leveranse av søknad");
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
        FPFordelKvittering fpFordelKvittering = FPFordelKvittering.class.cast(leveranseRespons.getBody());
        switch (leveranseRespons.getStatusCode()) {
        case ACCEPTED:
            if (fpFordelKvittering instanceof FPFordelPendingKvittering) {
                LOG.info("Søknaden er mottatt, men ikke forsøkt behandlet i FPSak");
                FPFordelPendingKvittering pending = FPFordelPendingKvittering.class.cast(leveranseRespons.getBody());
                URI pollURI = locationFra(leveranseRespons);
                for (int i = 1; i <= maxAntallForsøk; i++) {
                    LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                    ResponseEntity<FPFordelKvittering> fpInfoRespons = pollFPFordel(pollURI,
                            pending.getPollInterval().toMillis());
                    fpFordelKvittering = FPFordelKvittering.class.cast(fpInfoRespons.getBody());
                    LOG.info("Behandler poll respons {} etter {}ms", fpInfoRespons, timer.getTime());
                    switch (fpInfoRespons.getStatusCode()) {
                    case OK:
                        if (fpFordelKvittering instanceof FPFordelPendingKvittering) {
                            LOG.info("Fikk pending kvittering  på {}. forsøk", i);
                            pending = FPFordelPendingKvittering.class.cast(fpFordelKvittering);
                            continue;
                        }
                        if (fpFordelKvittering instanceof FPFordelGosysKvittering) {
                            LOG.info("Fikk Gosys kvittering  på {}. forsøk, returnerer etter {}ms", i, stop(timer));
                            return gosysKvittering(ref, FPFordelGosysKvittering.class.cast(fpFordelKvittering));
                        }
                        LOG.warn("Uventet kvittering {} for statuskode {}, gir opp (etter {}ms)", fpFordelKvittering,
                                fpInfoRespons.getStatusCode(), stop(timer));
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    case SEE_OTHER:
                        FPSakFordeltKvittering fordelt = FPSakFordeltKvittering.class.cast(fpFordelKvittering);
                        return poller.poll(locationFra(fpInfoRespons), ref, timer,
                                pending.getPollInterval(), fordelt);

                    default:
                        LOG.warn("Uventet responskode {} etter leveranse av søknad, gir opp (etter {}ms)",
                                fpInfoRespons.getStatusCode(),
                                timer.getTime());
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    }
                }
                LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp (etter {}ms)", maxAntallForsøk,
                        stop(timer));

                return new Kvittering(FP_FORDEL_MESSED_UP, ref);
            }
        default:
            LOG.warn("Uventet responskode {} ved leveranse av søknad, gir opp (etter {}ms)",
                    leveranseRespons.getStatusCode(),
                    stop(timer));
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private static long stop(StopWatch timer) {
        timer.stop();
        return timer.getTime();
    }

    private static URI locationFra(ResponseEntity<FPFordelKvittering> respons) {
        return Optional
                .ofNullable(respons.getHeaders().getFirst(LOCATION))
                .map(URI::create)
                .orElseThrow(IllegalArgumentException::new);
    }

    private ResponseEntity<FPFordelKvittering> pollFPFordel(URI uri, long delayMillis) {
        return poll(uri, "FPFordel", delayMillis, FPFordelKvittering.class);
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

    private static Kvittering kvitteringMedType(LeveranseStatus type, String ref, String journalId, String saksnr) {
        Kvittering kvittering = new Kvittering(type, ref);
        kvittering.setJournalId(journalId);
        kvittering.setSaksNr(saksnr);
        return kvittering;
    }

    private static Kvittering gosysKvittering(String ref, FPFordelGosysKvittering gosysKvittering) {
        LOG.info("Søknaden er sendt til manuell behandling i Gosys, journalId er {}",
                gosysKvittering.getJournalpostId());
        return kvitteringMedType(GOSYS, ref, gosysKvittering.getJournalpostId(), null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", maxAntallForsøk=" + maxAntallForsøk + "]";
    }
}
