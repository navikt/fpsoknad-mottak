package no.nav.foreldrepenger.lookup.ws.ytelser.fpsak;

import java.net.URI;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class FpsakHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final FpsakClient client;

    public FpsakHealthIndicator(FpsakClient client, Environment env,
            @Value("${VIRKSOMHET_FORELDREPENGESAK_V1_ENDPOINTURL}") URI serviceUrl) {
        super(env, serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
