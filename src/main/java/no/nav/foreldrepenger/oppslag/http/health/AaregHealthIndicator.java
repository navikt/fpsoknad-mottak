package no.nav.foreldrepenger.oppslag.http.health;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.aareg.AaregClient;

@Component
public class AaregHealthIndicator extends AbstractEnvAwareWSHealthIndicator {

    private final AaregClient client;

    public AaregHealthIndicator(AaregClient client, Environment env,
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") URI serviceUrl) {
        super(env, serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
