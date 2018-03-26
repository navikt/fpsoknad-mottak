package no.nav.foreldrepenger.oppslag.http.health;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.person.PersonClient;

@Component
public class TPSHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final PersonClient client;

    public TPSHealthIndicator(PersonClient client, Environment env,
            @Value("${VIRKSOMHET_PERSON_V3_ENDPOINTURL}") URI serviceUrl) {
        super(env, serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
