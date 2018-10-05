package no.nav.foreldrepenger.lookup.ws.medl;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;

@Component
public class MedlHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final MedlClient client;

    public MedlHealthIndicator(MedlClient client, @Value("${VIRKSOMHET_MEDLEMSKAP_V2_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
