package no.nav.foreldrepenger.lookup;

import java.net.URI;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;

public abstract class EnvironmentAwareServiceHealthIndicator implements HealthIndicator {

    private final URI serviceUrl;
    private boolean isPreprodOrDev;

    protected abstract void checkHealth();

    public EnvironmentAwareServiceHealthIndicator(Environment env, URI serviceUrl) {
        this.isPreprodOrDev = isPreprodOrDev(env);
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

    private Health downWithDetails(Exception e) {
        return Health.down().withException(e).build();
    }

    private boolean isPreprodOrDev(Environment env) {
        return env.acceptsProfiles("dev", "preprod");
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
