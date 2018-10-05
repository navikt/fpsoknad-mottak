package no.nav.foreldrepenger.mottak.http.health;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.isDevOrPreprod;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import no.nav.foreldrepenger.mottak.innsending.AbstractRestConnection;

abstract class EnvironmentAwareHealthIndicator implements HealthIndicator, EnvironmentAware {

    private final AbstractRestConnection connection;
    private Environment env;

    public EnvironmentAwareHealthIndicator(AbstractRestConnection connection) {
        this.connection = connection;
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    protected void checkHealth() {
        connection.ping();
    }

    @Override
    public Health health() {
        try {
            checkHealth();
            return isDevOrPreprod(env) ? upWithDetails() : up();
        } catch (Exception e) {
            return isDevOrPreprod(env) ? downWithDetails(e) : down();
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
        return getClass().getSimpleName() + " [connection=" + connection + "isDevOrPreprod "
                + isDevOrPreprod(env) + "]";
    }
}
