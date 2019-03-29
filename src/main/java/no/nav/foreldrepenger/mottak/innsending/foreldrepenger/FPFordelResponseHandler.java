package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.GOSYS;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FEILET_KVITTERINGER;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FORDELT_KVITTERING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.GITTOPP_KVITTERING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.MANUELL_KVITTERING;
import static no.nav.foreldrepenger.mottak.util.TimeUtil.waitFor;
import static org.springframework.http.HttpHeaders.LOCATION;

import java.net.URI;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsyn.FPInfoSaksPoller;

@Component
public class FPFordelResponseHandler extends AbstractRestConnection {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelResponseHandler.class);
    private final int maxAntallForsøk;
    private final long maxMillis;
    private final boolean pollfpinfo;
    private final FPInfoSaksPoller poller;

    public FPFordelResponseHandler(RestOperations restOperations,
            @Value("${fpfordel.max:10}") int maxAntallForsøk,
            @Value("${fpfordel.maxMillis:10000}") long maxMillis,
            @Value("${fpfordel.pollfpinfo:true}") boolean pollfpinfo,
            FPInfoSaksPoller poller) {
        super(restOperations);
        this.maxAntallForsøk = maxAntallForsøk;
        this.maxMillis = maxMillis;
        this.pollfpinfo = pollfpinfo;
        this.poller = poller;
    }

    public Kvittering handle(ResponseEntity<FPFordelKvittering> leveranseRespons) {
        StopWatch timer = new StopWatch();
        timer.start();
        if (!leveranseRespons.hasBody()) {
            LOG.warn("Fikk ingen kvittering etter leveranse av søknad");
            FEILET_KVITTERINGER.increment();
            return new Kvittering(FP_FORDEL_MESSED_UP);
        }
        LOG.info("Behandler respons {}", leveranseRespons.getBody());
        FPFordelKvittering fpFordelKvittering = FPFordelKvittering.class.cast(leveranseRespons.getBody());
        switch (leveranseRespons.getStatusCode()) {
        case ACCEPTED:
            if (fpFordelKvittering instanceof FPFordelPendingKvittering) {
                LOG.info("Søknaden er mottatt, men ennå ikke forsøkt behandlet i FPSak");
                FPFordelPendingKvittering pending = FPFordelPendingKvittering.class.cast(leveranseRespons.getBody());
                URI pollURI = locationFra(leveranseRespons);
                for (int i = 1; i <= maxAntallForsøk; i++) {
                    LOG.info("Poller {} for {}. gang av {}, medgått tid er {}ms av maks {}ms", pollURI, i,
                            maxAntallForsøk,
                            timer.getTime(), maxMillis);
                    if (timer.getTime() > maxMillis) {
                        LOG.info("Vi burde antagelig gi oss nå, brukt {}ms mer enn øvre grense",
                                maxMillis + timer.getTime());
                    }
                    ResponseEntity<FPFordelKvittering> fpInfoRespons = pollFPFordel(pollURI,
                            pending.getPollInterval().toMillis());
                    fpFordelKvittering = FPFordelKvittering.class.cast(fpInfoRespons.getBody());
                    LOG.info("Behandler poll respons {} etter {}ms", fpInfoRespons.getBody(), timer.getTime());
                    switch (fpInfoRespons.getStatusCode()) {
                    case OK:
                        if (fpFordelKvittering instanceof FPFordelPendingKvittering) {
                            LOG.info("Fikk pending kvittering  på {}. forsøk", i);
                            pending = FPFordelPendingKvittering.class.cast(fpFordelKvittering);
                            continue;
                        }
                        if (fpFordelKvittering instanceof FPFordelGosysKvittering) {
                            LOG.info("Fikk Gosys kvittering  på {}. forsøk, returnerer etter {}ms", i, stop(timer));
                            MANUELL_KVITTERING.increment();
                            return gosysKvittering(FPFordelGosysKvittering.class.cast(fpFordelKvittering));
                        }
                        LOG.warn("Uventet kvittering {} for statuskode {}, gir opp (etter {}ms)", fpFordelKvittering,
                                fpInfoRespons.getStatusCode(), stop(timer));
                        return new Kvittering(FP_FORDEL_MESSED_UP);
                    case SEE_OTHER:
                        FORDELT_KVITTERING.increment();
                        FPSakFordeltKvittering fordelt = FPSakFordeltKvittering.class.cast(fpFordelKvittering);
                        if (pollfpinfo) {
                            return poller.poll(locationFra(fpInfoRespons), timer, pending.getPollInterval(), fordelt);
                        }
                        LOG.info("Poller ikke fpinfo");
                        return Kvittering.kvitteringMedType(SENDT_OG_FORSØKT_BEHANDLET_FPSAK,
                                fordelt.getJournalpostId(), fordelt.getSaksnummer());
                    default:
                        FEILET_KVITTERINGER.increment();
                        LOG.warn("Uventet responskode {} etter leveranse av søknad, gir opp (etter {}ms)",
                                fpInfoRespons.getStatusCode(), timer.getTime());
                        return new Kvittering(FP_FORDEL_MESSED_UP);
                    }
                }
                LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp (etter {}ms)", maxAntallForsøk,
                        stop(timer));
                GITTOPP_KVITTERING.increment();
                return new Kvittering(FP_FORDEL_MESSED_UP);
            }
        default:
            FEILET_KVITTERINGER.increment();
            LOG.warn("Uventet responskode {} ved leveranse av søknad, gir opp (etter {}ms)",
                    leveranseRespons.getStatusCode(),
                    stop(timer));
            return new Kvittering(FP_FORDEL_MESSED_UP);
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
        return poll(uri, "FPFORDEL", delayMillis, FPFordelKvittering.class);
    }

    private <T> ResponseEntity<T> poll(URI uri, String name, long delayMillis, Class<T> clazz) {
        waitFor(delayMillis);
        return getForEntity(uri, clazz);
    }

    private static Kvittering kvitteringMedType(LeveranseStatus type, String journalId, String saksnr) {
        Kvittering kvittering = new Kvittering(type);
        kvittering.setJournalId(journalId);
        kvittering.setSaksNr(saksnr);
        return kvittering;
    }

    private static Kvittering gosysKvittering(FPFordelGosysKvittering gosysKvittering) {
        LOG.info("Søknaden er sendt til manuell behandling i Gosys, journalId er {}",
                gosysKvittering.getJournalpostId());
        return kvitteringMedType(GOSYS, gosysKvittering.getJournalpostId(), null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [maxAntallForsøk=" + maxAntallForsøk + "]";
    }
}
