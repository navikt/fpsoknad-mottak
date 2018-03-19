package no.nav.foreldrepenger.mottak.dokmot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DokmotHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotHealthIndicator.class);

    private final DokmotQueuePinger pinger;

    private final Environment env;

    public DokmotHealthIndicator(DokmotQueuePinger pinger, Environment env) {
        this.pinger = pinger;
        this.env = env;
    }

    @Override
    public Health health() {
        try {
            pinger.ping();
            return isPreprodOrDev() ? upWithDetails() : up();
        } catch (Exception e) {
            LOG.warn("Could not verify health of queue {}", pinger.getQueueConfig(), e);
            return isPreprodOrDev() ? downWithDetails(e) : down();
        }
    }

    private static Health down() {
        return Health.down().build();
    }

    private Health downWithDetails(Exception e) {
        return Health.down().withDetail("config", pinger.getQueueConfig().toString()).withException(e).build();
    }

    private boolean isPreprodOrDev() {
        return env.acceptsProfiles("dev", "preprod");
    }

    private static Health up() {
        return Health.up().build();
    }

    private Health upWithDetails() {
        return Health.up().withDetail("config", pinger.getQueueConfig().toString()).build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pinger=" + pinger + "]";
    }

}
