package no.nav.foreldrepenger.lookup.ws.ytelser.arena;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;

@Component
public class ArenaHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final ArenaClient client;

    public ArenaHealthIndicator(ArenaClient client,
            @Value("${VIRKSOMHET_YTELSESKONTRAKT_V3_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
