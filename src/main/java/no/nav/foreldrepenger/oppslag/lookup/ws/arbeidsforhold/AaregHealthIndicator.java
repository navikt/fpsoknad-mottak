package no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold;

import java.net.URI;

import no.nav.foreldrepenger.oppslag.lookup.EnvironmentAwareServiceHealthIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AaregHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final ArbeidsforholdClient client;

    public AaregHealthIndicator(ArbeidsforholdClient client, Environment env,
                                @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") URI serviceUrl) {
        super(env, serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
