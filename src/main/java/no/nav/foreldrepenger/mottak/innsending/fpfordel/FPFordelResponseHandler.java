package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.Kvittering;

public class FPFordelResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelResponseHandler.class);
    private final RestTemplate template;

    public FPFordelResponseHandler(RestTemplate template) {
        this.template = template;
    }

    public Kvittering handle(ResponseEntity<FPFordelKvittering> kvittering, String ref, int n) {

        /*
         * if (!kvittering.hasBody()) {
         * LOG.warn("FPFordel did not return the expected receipt"); return
         * FP_FORDEL_MESSED_UP.build(); }
         *
         * if (kvittering.getBody() instanceof FPFordelPendingKvittering) { String
         * pollURI = kvittering.getHeaders().get(HttpHeaders.LOCATION).get(0);
         * LOG.info("Mottok URI {}", pollURI); if (n > 0) { return poll(pollURI, ref, n
         * - 1); } return new Kvittering(ref, SENDT_FPSAK); } if (kvittering.getBody()
         * instanceof FPFordelGosysKvittering) { return new Kvittering(ref,
         * SENDT_GOSYS); } if (kvittering.getBody() instanceof FPSakFordeltKvittering) {
         * return new Kvittering(ref, SENDT_OG_MOTATT_FPSAK); }
         */
        throw new FPFordelUnavailableException(null);
    }

    private Kvittering poll(String pollURI, String ref, int n) {
        try {
            Thread.sleep(2000);
            return handle(template.getForEntity(pollURI, FPFordelKvittering.class), ref, n);
        } catch (RestClientException | InterruptedException e) {
            LOG.warn("Kunne ikke polle FPFordel på {}", pollURI, e);
            throw new FPFordelUnavailableException(e);
        }
    }
}
