package no.nav.foreldrepenger.oppslag.http.health;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;

@Component
public class InntektHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final InntektClient client;

    public InntektHealthIndicator(InntektClient client, Environment env,
            @Value("${VIRKSOMHET_INNTEKT_V3_ENDPOINTURL}") URI serviceUrl) {
        super(env, serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
