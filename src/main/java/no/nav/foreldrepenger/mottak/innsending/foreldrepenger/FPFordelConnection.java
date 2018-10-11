package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsending.AbstractRestConnection;

@Component
public class FPFordelConnection extends AbstractRestConnection implements Pingable {

    private final FPFordelConfig config;
    private final FPFordelResponseHandler responseHandler;

    public FPFordelConnection(RestTemplate template, FPFordelConfig config, FPFordelResponseHandler responseHandler) {
        super(template);
        this.config = config;
        this.responseHandler = responseHandler;
    }

    public Kvittering send(HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload, String ref) {
        return responseHandler.handle(postForEntity(tilURI(), payload, FPFordelKvittering.class), ref);
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return uri(config.getUri(), config.getPingPath());
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    private URI tilURI() {
        return uri(config.getUri(), config.getBasePath());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + ", responseHandler=" + responseHandler + "]";
    }

}
