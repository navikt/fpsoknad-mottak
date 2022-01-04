package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.domain.Kvittering.gosysKvittering;
import static no.nav.foreldrepenger.common.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.common.util.Constants.TOKENX;
import static no.nav.foreldrepenger.common.util.CounterRegistry.FEILET_KVITTERINGER;
import static no.nav.foreldrepenger.common.util.CounterRegistry.FORDELT_KVITTERING;
import static no.nav.foreldrepenger.common.util.CounterRegistry.GITTOPP_KVITTERING;
import static no.nav.foreldrepenger.common.util.CounterRegistry.MANUELL_KVITTERING;
import static no.nav.foreldrepenger.common.util.TimeUtil.waitFor;
import static org.springframework.http.HttpHeaders.LOCATION;

import java.net.URI;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FordelKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.GosysKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.PendingKvittering;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsyn.SakStatusPoller;

@Component
public class ResponseHandler extends AbstractRestConnection {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseHandler.class);
    private final int fpfordelMax;
    private final SakStatusPoller poller;

    public ResponseHandler(@Qualifier(TOKENX) RestOperations restOperations, @Value("${fpfordel.max:10}") int maxAntallForsøk,
            SakStatusPoller poller) {
        super(restOperations);
        this.fpfordelMax = maxAntallForsøk;
        this.poller = poller;
    }

    public Kvittering handle(ResponseEntity<FordelKvittering> leveranseRespons) {
        var timer = StopWatch.createStarted();
        if (!leveranseRespons.hasBody()) {
            LOG.warn("Fikk ingen kvittering etter leveranse av søknad");
            FEILET_KVITTERINGER.increment();
            return new Kvittering(FP_FORDEL_MESSED_UP);
        }
        LOG.info("Behandler respons {}", leveranseRespons.getBody());
        var fpFordelKvittering = FordelKvittering.class.cast(leveranseRespons.getBody());
        switch (leveranseRespons.getStatusCode()) {
            case ACCEPTED -> {
                if (fpFordelKvittering instanceof PendingKvittering pending) {
                    LOG.info("Søknaden er mottatt, men ennå ikke forsøkt behandlet i FPSak");
                    URI pollURI = locationFra(leveranseRespons);
                    for (int i = 1; i <= fpfordelMax; i++) {
                        LOG.info("Poller {} for {}. gang av {}, medgått tid er {}ms", pollURI, i,
                                fpfordelMax,
                                timer.getTime());
                        var fpInfoRespons = pollFPFordel(pollURI, pending.getPollInterval().toMillis());
                        fpFordelKvittering = FordelKvittering.class.cast(fpInfoRespons.getBody());
                        LOG.info("Behandler poll respons {} etter {}ms", fpInfoRespons.getBody(), timer.getTime());
                        switch (fpInfoRespons.getStatusCode()) {
                            case OK -> {
                                if (fpFordelKvittering instanceof PendingKvittering) {
                                    LOG.info("Fikk pending kvittering  på {}. forsøk", i);
                                    continue;
                                }
                                if (fpFordelKvittering instanceof GosysKvittering g) {
                                    LOG.info("Fikk Gosys kvittering  på {}. forsøk, returnerer etter {}ms", i,
                                            stop(timer));
                                    MANUELL_KVITTERING.increment();
                                    return gosysKvittering(g);
                                }
                                LOG.warn("Uventet kvittering {} for statuskode {}, gir opp (etter {}ms)",
                                        fpFordelKvittering,
                                        fpInfoRespons.getStatusCode(), stop(timer));
                                return new Kvittering(FP_FORDEL_MESSED_UP);
                            }
                            case SEE_OTHER -> {
                                FORDELT_KVITTERING.increment();
                                var fordelt = FPSakFordeltKvittering.class.cast(fpFordelKvittering);
                                return poller.poll(locationFra(fpInfoRespons), timer, pending.getPollInterval(),
                                        fordelt);
                            }
                            default -> {
                                FEILET_KVITTERINGER.increment();
                                LOG.warn("Uventet responskode {} etter leveranse av søknad, gir opp (etter {}ms)",
                                        fpInfoRespons.getStatusCode(), timer.getTime());
                                return new Kvittering(FP_FORDEL_MESSED_UP);
                            }
                        }
                    }
                    LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp (etter {}ms)", fpfordelMax,
                            stop(timer));
                    GITTOPP_KVITTERING.increment();
                    return new Kvittering(FP_FORDEL_MESSED_UP);
                }
            }
            default -> {
                FEILET_KVITTERINGER.increment();
                LOG.warn("Uventet responskode {} ved leveranse av søknad, gir opp (etter {}ms)",
                        leveranseRespons.getStatusCode(),
                        stop(timer));
                return new Kvittering(FP_FORDEL_MESSED_UP);
            }
        }
        return new Kvittering(FP_FORDEL_MESSED_UP);
    }

    private static long stop(StopWatch timer) {
        timer.stop();
        return timer.getTime();
    }

    private static URI locationFra(ResponseEntity<FordelKvittering> respons) {
        return Optional
                .ofNullable(respons.getHeaders().getFirst(LOCATION))
                .map(URI::create)
                .orElseThrow(IllegalArgumentException::new);
    }

    private ResponseEntity<FordelKvittering> pollFPFordel(URI uri, long delayMillis) {
        return poll(uri, delayMillis, FordelKvittering.class);
    }

    private <T> ResponseEntity<T> poll(URI uri, long delayMillis, Class<T> clazz) {
        waitFor(delayMillis);
        return getForEntity(uri, clazz);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fpfordelMax=" + fpfordelMax + ", poller=" + poller + "]";
    }

}
