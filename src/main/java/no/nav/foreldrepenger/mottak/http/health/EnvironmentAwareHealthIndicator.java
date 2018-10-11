package no.nav.foreldrepenger.mottak.http.health;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.isDevOrPreprod;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.Pingable;

abstract class EnvironmentAwareHealthIndicator implements HealthIndicator, EnvironmentAware {

    private final Pingable pingable;
    private Environment env;

    public EnvironmentAwareHealthIndicator(Pingable pingable) {
        this.pingable = pingable;
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    protected void checkHealth() {
        pingable.ping();
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
        return Health.down().withDetail("url", pingable.pingEndpoint()).withException(e).build();
    }

    private static Health up() {
        return Health.up().build();
    }

    private Health upWithDetails() {
        return Health.up().withDetail("url", pingable.pingEndpoint()).build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingable=" + pingable + "isDevOrPreprod "
                + isDevOrPreprod(env) + "]";
    }
}
