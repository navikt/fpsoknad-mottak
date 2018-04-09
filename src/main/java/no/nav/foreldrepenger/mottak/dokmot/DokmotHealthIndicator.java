package no.nav.foreldrepenger.mottak.dokmot;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

@Component
public class DokmotHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotHealthIndicator.class);

    private final DokmotQueuePinger pinger;

    private final Counter dokmotSuccess = Metrics.counter("dokmot.health", "response", "success");
    private final Counter dokmotFailure = Metrics.counter("dokmot.health", "response", "failure");

    private Environment env;

    public DokmotHealthIndicator(DokmotQueuePinger pinger, Environment env) {
        this.pinger = pinger;
        this.env = env;

    }

    @Override
    public Health health() {
        if (isDev()) {
            try {
                pinger.ping();
                dokmotSuccess.increment();
                return isPreprod() ? upWithDetails() : up();
            } catch (Exception e) {
                dokmotFailure.increment();
                LOG.warn("Could not verify health of DOKMOT {}", pinger.getQueueConfig(), e);
                return isPreprod() ? downWithDetails(e) : down();
            }
        }
        LOG.info("In DEV mode, not verifying health of DOKMOT");
        return up();
    }

    private static Health down() {
        return Health.down().build();
    }

    private Health downWithDetails(Exception e) {
        return Health.down().withDetail("config", pinger.getQueueConfig().toString()).withException(e).build();
    }

    private boolean isPreprod() {
        return env.acceptsProfiles("preprod");
    }

    private boolean isDev() {
        return env.acceptsProfiles("dev");
    }

    private static Health up() {
        return Health.up().build();
    }

    private Health upWithDetails() {
        return Health.up().withDetail("config", pinger.getQueueConfig().toString()).build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pinger=" + pinger + "activeProfiles "
                + Arrays.toString(env.getActiveProfiles()) + "]";
    }

}
