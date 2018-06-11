package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_FPSAK;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.domain.Kvittering;

@Component
public class FPFordelConnection {

    private static final String FPFORDEL_PATH = "/fpfordel/api/dokumentforsendelse";

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
        String pingEndpoint = config.getUri() + "/internal/isReady";
        LOG.info("Pinger {}", pingEndpoint);
        try {
            ResponseEntity<String> response = template.getForEntity(pingEndpoint, String.class);
            LOG.info("Fikk response entity {} ({})", response.getBody(), response.getStatusCodeValue());
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke pinge FPFordel på {}", pingEndpoint, e);
            throw new FPFordelUnavailableException(e);
        }
    }

    public Kvittering send(HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload, String ref) {
        URI postEndpoint = UriComponentsBuilder
                .fromUriString(config.getUri())
                .pathSegment(FPFORDEL_PATH)
                .build()
                .toUri();
        LOG.info("Poster til {}", postEndpoint);
        try {
            // TODO check what we get, poll until timeout
            URI pollURI = template.postForLocation(postEndpoint, payload);
            LOG.info("Mottok URI {}", pollURI);
            return new Kvittering(SENDT_FPSAK);
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke poste til FPFordel på {}", postEndpoint, e);
            throw new FPFordelUnavailableException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", config=" + config + "]";
    }
}
