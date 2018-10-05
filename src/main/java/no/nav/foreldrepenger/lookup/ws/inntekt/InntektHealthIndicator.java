package no.nav.foreldrepenger.lookup.ws.inntekt;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;

@Component
public class InntektHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final InntektClient client;

    public InntektHealthIndicator(InntektClient client, @Value("${VIRKSOMHET_INNTEKT_V3_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
