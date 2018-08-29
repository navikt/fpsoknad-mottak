package no.nav.foreldrepenger.lookup.rest.fpinfo;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;

@Component
public class FPFordelHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final FPInfoConnection connection;

    public FPFordelHealthIndicator(Environment env, FPInfoConnection connection) {
        super(env, connection.pingEndpoint());
        this.connection = connection;
    }

    @Override
    protected void checkHealth() {
        connection.ping();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "]";
    }
}
