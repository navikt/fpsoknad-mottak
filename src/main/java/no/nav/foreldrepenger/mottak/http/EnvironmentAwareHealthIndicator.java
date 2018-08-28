package no.nav.foreldrepenger.mottak.http;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;

import no.nav.foreldrepenger.mottak.innsending.AbstractFPConnection;
import no.nav.foreldrepenger.mottak.util.EnvUtil;

public abstract class EnvironmentAwareHealthIndicator implements HealthIndicator {

    private final AbstractFPConnection connection;
    private boolean isPreprodOrDev;

    public EnvironmentAwareHealthIndicator(Environment env, AbstractFPConnection connection) {
        this.isPreprodOrDev = EnvUtil.isDevOrPreprod(env);
        this.connection = connection;
    }

    protected void checkHealth() {
        connection.ping();
    }

    @Override
    public Health health() {
        try {
            checkHealth();
            return isPreprodOrDev ? upWithDetails() : up();
        } catch (Exception e) {
            return isPreprodOrDev ? downWithDetails(e) : down();
        }
    }

    private static Health down() {
        return Health.down().build();
    }

    private Health downWithDetails(Exception e) {
        return Health.down().withDetail("url", connection.pingEndpoint()).withException(e).build();
    }

    private static Health up() {
        return Health.up().build();
    }

    private Health upWithDetails() {
        return Health.up().withDetail("url", connection.pingEndpoint()).build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "isPreprodOrDev "
                + isPreprodOrDev + "]";
    }
}
