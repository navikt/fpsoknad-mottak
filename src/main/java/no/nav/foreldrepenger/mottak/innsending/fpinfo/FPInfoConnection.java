package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.innsending.AbstractRestConnection;

@Component
public class FPInfoConnection extends AbstractRestConnection {

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
        return config.pingEndpoint();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", config=" + config + "]";
    }

}
