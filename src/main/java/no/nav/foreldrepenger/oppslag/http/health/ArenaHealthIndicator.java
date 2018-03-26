package no.nav.foreldrepenger.oppslag.http.health;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.arena.ArenaClient;

@Component
public class ArenaHealthIndicator extends AbstractEnvAwareWSHealthIndicator {

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
