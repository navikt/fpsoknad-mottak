package no.nav.foreldrepenger.oppslag.http.health;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;

@Component
public class AktørHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(AktørHealthIndicator.class);

    private final AktorIdClient client;

    private final Environment env;

    public AktørHealthIndicator(AktorIdClient client, Environment env) {
        this.client = client;
        this.env = env;
    }

    @Override
    public Health health() {
        try {
            client.ping();
            return isPreprodOrDev() ? upWithDetails() : up();
        } catch (Exception e) {
            return isPreprodOrDev() ? downWithDetails(e) : down();
        }
    }

    private static Health down() {
        return Health.down().build();
    }

    private Health downWithDetails(Exception e) {
        return Health.down().withException(e).build();
    }

    private boolean isPreprodOrDev() {
        return env.acceptsProfiles("dev", "preprod");
    }

    private static Health up() {
        return Health.up().build();
    }

    private Health upWithDetails() {
        return Health.up().build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [client=" + client + "activeProfiles "
                + Arrays.toString(env.getActiveProfiles()) + "]";
    }
}
