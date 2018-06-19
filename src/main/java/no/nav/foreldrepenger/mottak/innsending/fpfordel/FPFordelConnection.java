package no.nav.foreldrepenger.mottak.innsending.fpfordel;

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

    private static final String PING_PATH = "fpfordel/internal/isReady";

    private static final String FPFORDEL_PATH = "fpfordel/api/dokumentforsendelse";

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelConnection.class);

    private final RestTemplate template;
    private final FPFordelConfig config;
    private final FPFordelResponseHandler responseHandler;

    public FPFordelConnection(RestTemplate template, FPFordelConfig config, FPFordelResponseHandler responseHandler) {
        this.template = template;
        this.config = config;
        this.responseHandler = responseHandler;
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    public void ping() {
        URI pingEndpoint = endpointFor(PING_PATH);
        LOG.info("Pinger {}", pingEndpoint);
        try {
            ResponseEntity<String> response = template.getForEntity(pingEndpoint, String.class);
            LOG.info("Fikk response entity {} ({})", response.getBody(), response.getStatusCodeValue());
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke pinge FPFordel på {}", pingEndpoint, e);
            throw new FPFordelUnavailableException(e);
        }
    }

    URI pingEndpoint() {
        return endpointFor(PING_PATH);
    }

    public Kvittering send(HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload, String ref) {
        URI postEndpoint = endpointFor(FPFORDEL_PATH);
        try {
            LOG.info("Sender søknad til {}", postEndpoint);
            return responseHandler.handle(template.postForEntity(postEndpoint, payload,
                    FPFordelKvittering.class), ref);
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke poste til FPFordel på {}", postEndpoint, e);
            throw new FPFordelUnavailableException(e);
        }
    }

    public URI endpointFor(String path) {
        return UriComponentsBuilder
                .fromUriString(config.getUri())
                .pathSegment(path)
                .build().toUri();

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", config=" + config + ", responseHandler="
                + responseHandler + "]";
    }

}
