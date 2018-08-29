package no.nav.foreldrepenger.lookup.rest.fpinfo;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.lookup.rest.AbstractRestConnection;

@Component
public class FPInfoConnection extends AbstractRestConnection {

    private static final String PING_PATH = "fpinfo/internal/isReady";

    private final FPInfoConfig config;

    public FPInfoConnection(RestTemplate template, FPInfoConfig config) {
        super(template);
        this.config = config;
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public URI pingEndpoint() {
        return endpointFor(config.getBaseURL(), PING_PATH);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", config=" + config + "]";
    }

}
