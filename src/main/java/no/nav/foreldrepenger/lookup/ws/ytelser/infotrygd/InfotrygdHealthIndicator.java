package no.nav.foreldrepenger.lookup.ws.ytelser.infotrygd;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;

@Component
public class InfotrygdHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final InfotrygdClient client;

    public InfotrygdHealthIndicator(InfotrygdClient client,
            @Value("${VIRKSOMHET_INFOTRYGDSAK_V1_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
