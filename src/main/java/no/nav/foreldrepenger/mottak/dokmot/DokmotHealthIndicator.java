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

    private final QueuePinger pinger;

    @Inject
    public DokmotHealthIndicator(QueuePinger pinger) {
        this.pinger = pinger;
    }

    @Override
    public Health health() {
        try {
            pinger.ping();
            return Health.up().build();
        } catch (RemoteUnavailableException e) {
            LOG.warn("Could not verify health of queue", e);
            return Health.down().withException(e).build();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pinger=" + pinger + "]";
    }

}
