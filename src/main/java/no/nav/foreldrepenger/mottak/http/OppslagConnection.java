package no.nav.foreldrepenger.mottak.http;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.innsending.AbstractRestConnection;

@Component
public class OppslagConnection extends AbstractRestConnection {

    private final OppslagConfig config;

    public OppslagConnection(RestTemplate template, OppslagConfig config) {
        super(template);
        this.config = config;
    }

    @Override
    public URI pingEndpoint() {
        return config.pingEndpoint();
    }

    public URI baseURI() {
        return config.getUrl();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }

}
