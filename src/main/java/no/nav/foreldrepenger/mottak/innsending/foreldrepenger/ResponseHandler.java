package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.util.CounterRegistry.FEILET_KVITTERINGER;
import static no.nav.foreldrepenger.common.util.CounterRegistry.FORDELT_KVITTERING;
import static no.nav.foreldrepenger.common.util.CounterRegistry.GITTOPP_KVITTERING;
import static no.nav.foreldrepenger.common.util.CounterRegistry.MANUELL_KVITTERING;
import static no.nav.foreldrepenger.common.util.TimeUtil.waitFor;
import static org.springframework.http.HttpHeaders.LOCATION;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.common.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FordelKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.GosysKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.PendingKvittering;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;

@Component
public class ResponseHandler extends AbstractRestConnection {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseHandler.class);
    private final int fpfordelMax;

    public ResponseHandler(RestOperations restOperations, @Value("${fpfordel.max:10}") int maxAntallForsøk) {
        super(restOperations);
        this.fpfordelMax = maxAntallForsøk;
    }

    public FordelResultat handle(ResponseEntity<FordelKvittering> leveranseRespons) {
        if (!leveranseRespons.hasBody()) {
            LOG.warn("Fikk ingen kvittering etter leveranse av søknad");
            FEILET_KVITTERINGER.increment();
            throw uventetFordelException(leveranseRespons);
        }
        LOG.info("Behandler respons {}", leveranseRespons.getBody());
        var fpFordelKvittering = leveranseRespons.getBody();
        if (leveranseRespons.getStatusCode() == HttpStatus.ACCEPTED) {
            if (fpFordelKvittering instanceof PendingKvittering pending) {
                return handlePendingKvittering(leveranseRespons, pending);
            }
            LOG.warn("Uventet type kvittering {} ved 202 fra fpfordel", fpFordelKvittering);
        } else {
            FEILET_KVITTERINGER.increment();
            LOG.warn("Uventet responskode {} ved leveranse av søknad, gir opp",
                leveranseRespons.getStatusCode());
            throw uventetFordelException(leveranseRespons);
        }
        throw uventetFordelException(leveranseRespons);
    }

    private UventetFpFordelResponseException uventetFordelException(ResponseEntity<FordelKvittering> leveranseRespons) {
         return new UventetFpFordelResponseException(leveranseRespons.getStatusCode());
    }

    private FordelResultat handlePendingKvittering(ResponseEntity<FordelKvittering> leveranseRespons,
                                                   PendingKvittering pendingKvittering) {
        LOG.info("Søknaden er mottatt, men ennå ikke forsøkt behandlet i FPSak");
        var pollURI = locationFra(leveranseRespons);
        for (var i = 1; i <= fpfordelMax; i++) {
            LOG.info("Poller {} for {}. gang av {}", pollURI, i, fpfordelMax);
            var fpFordelResponse = pollFPFordel(pollURI, pendingKvittering.getPollInterval().toMillis());
            var fpFordelKvittering = fpFordelResponse.getBody();
            LOG.info("Behandler poll respons {}", fpFordelResponse.getBody());
            switch (fpFordelResponse.getStatusCode()) {
                case OK -> {
                    if (fpFordelKvittering instanceof PendingKvittering) {
                        LOG.info("Fikk pending kvittering på {}. forsøk", i);
                        continue;
                    }
                    if (fpFordelKvittering instanceof GosysKvittering g) {
                        LOG.info("Fikk Gosys kvittering på {}. forsøk", i);
                        MANUELL_KVITTERING.increment();
                        LOG.info("Søknaden er sendt til manuell behandling i Gosys, journalId er {}",
                            g.getJournalpostId());
                        return new FordelResultat(g.getJournalpostId(), null);
                    }
                    LOG.warn("Uventet kvittering {} for statuskode {}, gir opp", fpFordelKvittering, fpFordelResponse.getStatusCode());
                    throw uventetFordelException(leveranseRespons);
                }
                case SEE_OTHER -> {
                    FORDELT_KVITTERING.increment();
                    var fordelt = (FPSakFordeltKvittering) fpFordelKvittering;
                    if (fordelt == null) {
                        LOG.warn("Fpfordel svarte 303, men body er null");
                        throw uventetFordelException(leveranseRespons);
                    }
                    return new FordelResultat(fordelt.getJournalpostId(), fordelt.getSaksnummer());
                }
                default -> {
                    FEILET_KVITTERINGER.increment();
                    LOG.warn("Uventet responskode {} etter leveranse av søknad, gir opp",
                        fpFordelResponse.getStatusCode());
                    throw uventetFordelException(leveranseRespons);
                }
            }
        }
        LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp", fpfordelMax);
        GITTOPP_KVITTERING.increment();
        throw uventetFordelException(leveranseRespons);
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
        return getClass().getSimpleName() + " [fpfordelMax=" + fpfordelMax + "]";
    }

}
