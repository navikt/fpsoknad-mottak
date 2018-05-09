package no.nav.foreldrepenger.mottak.fpfordel;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FPFordelConnection {

    private final RestTemplate template;
    private final FPFordelConfig config;

    public FPFordelConnection(RestTemplate template, FPFordelConfig config) {
        this.template = template;
        this.config = config;
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", config=" + config + "]";
    }
}
