package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class GsakHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final GsakClient client;

    public GsakHealthIndicator(GsakClient client, Environment env,
                               @Value("${VIRKSOMHET_SAK_V2_ENDPOINTURL}") URI serviceUrl) {
        super(env, serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
