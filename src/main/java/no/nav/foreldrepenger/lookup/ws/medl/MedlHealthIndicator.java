package no.nav.foreldrepenger.lookup.ws.medl;

import java.net.URI;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MedlHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final MedlClient client;

    public MedlHealthIndicator(MedlClient client, Environment env,
            @Value("${VIRKSOMHET_MEDLEMSKAP_V2_ENDPOINTURL}") URI serviceUrl) {
        super(env, serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
