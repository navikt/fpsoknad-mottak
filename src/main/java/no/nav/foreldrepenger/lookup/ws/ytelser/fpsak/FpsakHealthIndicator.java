package no.nav.foreldrepenger.lookup.ws.ytelser.fpsak;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;

@Component
public class FpsakHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final FpsakClient client;

    public FpsakHealthIndicator(FpsakClient client,
            @Value("${VIRKSOMHET_FORELDREPENGESAK_V1_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
