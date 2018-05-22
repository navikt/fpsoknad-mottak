package no.nav.foreldrepenger.mottak.fpfordel;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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
            LOG.warn("Kunne ikke pinge {}", pingEndpoint, e);
            throw new FPFordelUnavailableException(e);
        }
    }

    public URI send(HttpEntity entity) {
        try {
            return template.postForLocation(config.getUri(),
                    new org.springframework.http.HttpEntity<>(EntityUtils.toByteArray(entity)));
        } catch (IOException e) {
            throw new FPFordelUnavailableException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", config=" + config + "]";
    }
}
