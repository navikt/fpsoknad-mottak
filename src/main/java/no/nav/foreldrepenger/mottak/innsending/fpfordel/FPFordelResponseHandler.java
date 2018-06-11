package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_GOSYS;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_MOTATT_FPSAK;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.OK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.Kvittering;

@Component
public class FPFordelResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelResponseHandler.class);
    private final RestTemplate template;
    private final int maxAntallForsøk;

    public FPFordelResponseHandler(RestTemplate template, @Value("${fpfordel.max:3}") int maxAntallForsøk) {
        this.template = template;
        this.maxAntallForsøk = maxAntallForsøk;
    }

    public Kvittering handle(ResponseEntity<FPFordelKvittering> kvittering, String ref) {
        return handle(kvittering, ref, maxAntallForsøk);
    }

    public Kvittering handle(ResponseEntity<FPFordelKvittering> kvittering, String ref, int n) {

        if (kvittering.getStatusCode() != OK) {
            LOG.warn("FPFordel returnerte ikke forventet statuskode 200, fikk {}", kvittering.getStatusCode());
            return new Kvittering(FP_FORDEL_MESSED_UP);
        }

        if (!kvittering.hasBody()) {
            LOG.warn("FPFordel returnerte ikke forventet kvittering");
            String location = kvittering.getHeaders().getFirst(LOCATION);
            if (location != null && location.contains("redirect_url")) {
                LOG.warn("Open AM roter det til for oss ({)}", location);
            }
            return new Kvittering(FP_FORDEL_MESSED_UP);
        }

        if (kvittering.getBody() instanceof FPFordelPendingKvittering) {
            FPFordelPendingKvittering pendingKvittering = FPFordelPendingKvittering.class.cast(kvittering.getBody());
            String pollURI = kvittering.getHeaders().getFirst(LOCATION);
            if (pollURI == null) {
                LOG.warn("FPFordel returnerte ikke forventet URI for polling");
                return new Kvittering(FP_FORDEL_MESSED_UP);
            }
            if (n >= 0) {
                return poll(pollURI, ref, pendingKvittering.getPollInterval().toMillis(), n - 1);
            }
            LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp", maxAntallForsøk);
            return new Kvittering(ref, SENDT_FPSAK);
        }
        if (kvittering.getBody() instanceof FPFordelGosysKvittering) {
            LOG.info("Søknaden er sendt til manuell behandling");
            return new Kvittering(ref, SENDT_GOSYS);
        }
        if (kvittering.getBody() instanceof FPSakFordeltKvittering) {
            LOG.info("Søknaden er sendt og motatt av FPSak");
            return new Kvittering(ref, SENDT_OG_MOTATT_FPSAK);
        }
        throw new FPFordelUnavailableException("Uventet respons fra FPFordel " + kvittering.getBody());
    }

    private Kvittering poll(String pollURI, String ref, long pollDuration, int n) {
        try {
            LOG.info("Poller URI {}", pollURI);
            Thread.sleep(pollDuration);
            return handle(template.getForEntity(pollURI, FPFordelKvittering.class), ref, n);
        } catch (RestClientException | InterruptedException e) {
            LOG.warn("Kunne ikke polle FPFordel på {}", pollURI, e);
            throw new FPFordelUnavailableException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", maxAntallForsøk=" + maxAntallForsøk + "]";
    }
}
