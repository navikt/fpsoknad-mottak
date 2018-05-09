package no.nav.foreldrepenger.mottak.fpfordel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.http.FPFordelMottakController;

@Component
public class FPFordelConnection {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelConnection.class);

    private final RestTemplate template;
    private final FPFordelConfig config;

    public FPFordelConnection(RestTemplate template, FPFordelConfig config) {
        this.template = template;
        this.config = config;
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    public void ping() {
        LOG.info("Pinger {}", config.getUri());
        try {
            ResponseEntity<String> response = template.getForEntity(
                    FPFordelMottakController.FPFORDEL + "/internal/isReady",
                    String.class);
            LOG.info("Got response entity {} ({})", response.getBody(), response.getStatusCodeValue());
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke pinge {}", config.getUri(), e);
            throw new FPFordelUnavailableException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", config=" + config + "]";
    }
}
