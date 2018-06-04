package no.nav.foreldrepenger.mottak.fpfordel;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

    public URI send(HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        String postEndpoint = config.getUri() + "/fpfordel/api/dokumentforsendelse";
        LOG.info("Poster til {}", postEndpoint);
        try {
            URI pollURI = template.postForLocation(postEndpoint, payload);
            LOG.info("Received URI {}", pollURI);
            return pollURI;
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
