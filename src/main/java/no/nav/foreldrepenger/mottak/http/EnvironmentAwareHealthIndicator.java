package no.nav.foreldrepenger.mottak.http;

import java.net.URI;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;

import no.nav.foreldrepenger.mottak.util.EnvUtil;

public abstract class EnvironmentAwareHealthIndicator implements HealthIndicator {

    private final URI serviceUrl;
    private boolean isPreprodOrDev;

    protected abstract void checkHealth();

    public EnvironmentAwareHealthIndicator(Environment env, URI serviceUrl) {
        this.isPreprodOrDev = EnvUtil.isDevOrPreprod(env);
        this.serviceUrl = serviceUrl;
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

    private static Health downWithDetails(Exception e) {
        return Health.down().withException(e).build();
    }

    private static Health up() {
        return Health.up().build();
    }

    private Health upWithDetails() {
        return Health.up().withDetail("url", serviceUrl).build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [url=" + serviceUrl + "isPreprodOrDev "
                + isPreprodOrDev + "]";
    }
}
