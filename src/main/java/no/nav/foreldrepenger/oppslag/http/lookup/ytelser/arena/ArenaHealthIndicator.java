package no.nav.foreldrepenger.oppslag.http.lookup.ytelser.arena;

import java.net.URI;

import no.nav.foreldrepenger.oppslag.http.lookup.EnvironmentAwareServiceHealthIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ArenaHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final ArenaClient client;

    public ArenaHealthIndicator(ArenaClient client, Environment env,
            @Value("${VIRKSOMHET_YTELSESKONTRAKT_V3_ENDPOINTURL}") URI serviceUrl) {
        super(env, serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
