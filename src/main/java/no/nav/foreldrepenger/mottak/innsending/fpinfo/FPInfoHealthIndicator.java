package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.http.EnvironmentAwareHealthIndicator;

@Component
public class FPInfoHealthIndicator extends EnvironmentAwareHealthIndicator {

    private final FPInfoConnection connection;

    public FPInfoHealthIndicator(Environment env, FPInfoConnection connection) {
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
