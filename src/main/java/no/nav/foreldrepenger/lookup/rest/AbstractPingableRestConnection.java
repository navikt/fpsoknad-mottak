package no.nav.foreldrepenger.lookup.rest;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.lookup.Pingable;
import no.nav.foreldrepenger.lookup.rest.fpinfo.RemoteUnavailableException;

public abstract class AbstractPingableRestConnection implements Pingable {

    private final RestTemplate template;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPingableRestConnection.class);

    protected abstract URI pingEndpoint();

    public AbstractPingableRestConnection(RestTemplate template) {
        this.template = template;
    }

    @Override
    public void ping() {
        URI pingEndpoint = pingEndpoint();
        try {
            LOG.info("Pinger {}", pingEndpoint);
            ResponseEntity<String> response = template.getForEntity(pingEndpoint, String.class);
            LOG.info("Fikk response entity {} ({})", response.getBody(), response.getStatusCodeValue());
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke pinge {}", pingEndpoint, e);
            throw new RemoteUnavailableException(pingEndpoint, e);
        }
    }

    protected static URI endpointFor(String base, String path) {
        return UriComponentsBuilder
                .fromUriString(base)
                .pathSegment(path)
                .build().toUri();

    }
}
