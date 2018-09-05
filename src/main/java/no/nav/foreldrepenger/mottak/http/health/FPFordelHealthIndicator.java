package no.nav.foreldrepenger.mottak.http.health;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelConnection;

@Component
public class FPFordelHealthIndicator extends EnvironmentAwareHealthIndicator {

    public FPFordelHealthIndicator(Environment env, FPFordelConnection connection) {
        super(env, connection);
    }
}
