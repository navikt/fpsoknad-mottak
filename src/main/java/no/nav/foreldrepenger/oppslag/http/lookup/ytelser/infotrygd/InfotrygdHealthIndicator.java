package no.nav.foreldrepenger.oppslag.http.lookup.ytelser.infotrygd;

import java.net.URI;

import no.nav.foreldrepenger.oppslag.http.lookup.EnvironmentAwareServiceHealthIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class InfotrygdHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final InfotrygdClient client;

    public InfotrygdHealthIndicator(InfotrygdClient client, Environment env,
            @Value("${VIRKSOMHET_INFOTRYGDSAK_V1_ENDPOINTURL}") URI serviceUrl) {
        super(env, serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
