package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import java.net.URI;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.http.EnvironmentAwareHealthIndicator;

@Component
public class FPFordelHealthIndicator extends EnvironmentAwareHealthIndicator {

    private final FPFordelConnection connection;

    public FPFordelHealthIndicator(Environment env, URI serviceUrl, FPFordelConnection connection) {
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
