package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.http.EnvironmentAwareHealthIndicator;

@Component
public class FPFordelHealthIndicator extends EnvironmentAwareHealthIndicator {

    public FPFordelHealthIndicator(Environment env, FPFordelConnection connection) {
        super(env, connection);
    }
}
