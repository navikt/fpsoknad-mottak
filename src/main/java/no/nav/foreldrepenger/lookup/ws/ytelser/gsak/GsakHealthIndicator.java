package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;

@Component
public class GsakHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final GsakClient client;

    public GsakHealthIndicator(GsakClient client, @Value("${VIRKSOMHET_SAK_V2_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [client=" + client + "]";
    }
}
