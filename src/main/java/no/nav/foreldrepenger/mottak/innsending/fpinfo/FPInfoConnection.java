package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.http.RemoteUnavailableException;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelConnection;

@Component
public class FPInfoConnection {

    private static final String PING_PATH = "/fpinfo/internal/isReady";

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelConnection.class);

    private final RestTemplate template;
    private final FPInfoConfig config;

    public FPInfoConnection(RestTemplate template, FPInfoConfig config) {
        this.template = template;
        this.config = config;
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    public String ping() {
        URI pingEndpoint = endpointFor(PING_PATH);
        LOG.info("Pinger {}", pingEndpoint);
        try {
            ResponseEntity<String> response = template.getForEntity(pingEndpoint, String.class);
            LOG.info("Fikk response entity {} ({})", response.getBody(), response.getStatusCodeValue());
            return response.getBody();
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke pinge FPInfo på {}", pingEndpoint, e);
            throw new RemoteUnavailableException(e);
        }
    }

    URI pingEndpoint() {
        return endpointFor(PING_PATH);
    }

    private URI endpointFor(String path) {
        return UriComponentsBuilder
                .fromUriString(config.getFpinfo())
                .pathSegment(path)
                .build().toUri();

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", config=" + config + "]";
    }

}
