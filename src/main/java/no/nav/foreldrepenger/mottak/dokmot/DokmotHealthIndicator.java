package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DokmotHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotHealthIndicator.class);

    private final DokmotQueuePinger pinger;

    @Inject
    public DokmotHealthIndicator(DokmotQueuePinger pinger) {
        this.pinger = pinger;
    }

    @Override
    public Health health() {
        try {
            pinger.ping();
            return Health.up().build();
        } catch (DokmotQueueUnavailableException e) {
            LOG.warn("Could not verify health of queue {}", pinger.getQueueConfig(), e);
            return Health.down().withException(e).build();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pinger=" + pinger + "]";
    }

}
