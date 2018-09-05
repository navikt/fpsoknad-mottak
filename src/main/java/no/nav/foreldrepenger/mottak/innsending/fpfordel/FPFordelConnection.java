package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.http.errorhandling.RemoteUnavailableException;
import no.nav.foreldrepenger.mottak.innsending.AbstractRestConnection;

@Component
public class FPFordelConnection extends AbstractRestConnection {

    private static final String PING_PATH = "fpfordel/internal/isReady";

    private static final String FPFORDEL_PATH = "fpfordel/api/dokumentforsendelse";

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelConnection.class);

    private final FPFordelConfig config;
    private final FPFordelResponseHandler responseHandler;

    public FPFordelConnection(RestTemplate template, FPFordelConfig config, FPFordelResponseHandler responseHandler) {
        super(template);
        this.config = config;
        this.responseHandler = responseHandler;
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    public Kvittering send(HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload, String ref) {
        URI postEndpoint = endpointFor(config.getUri(), FPFORDEL_PATH);
        try {
            LOG.info("Sender søknad til {}", postEndpoint);
            return responseHandler.handle(template.postForEntity(postEndpoint, payload, FPFordelKvittering.class), ref);
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke poste til FPFordel på {}", postEndpoint, e);
            throw new RemoteUnavailableException(postEndpoint, e);
        }
    }

    @Override
    public URI pingEndpoint() {
        return endpointFor(config.getUri(), PING_PATH);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", config=" + config + ", responseHandler="
                + responseHandler + "]";
    }

}
