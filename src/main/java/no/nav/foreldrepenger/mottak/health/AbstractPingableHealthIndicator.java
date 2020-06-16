package no.nav.foreldrepenger.mottak.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;

public abstract class AbstractPingableHealthIndicator implements HealthIndicator {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPingableHealthIndicator.class);

    private final PingEndpointAware pingable;

    public AbstractPingableHealthIndicator(PingEndpointAware pingable) {
        this.pingable = pingable;
    }

    @Override
    public Health health() {
        try {
            LOG.trace("Pinger {} på {}", pingable.name(), pingable.pingEndpoint());
            var response = pingable.ping();
            LOG.trace("Ping {} fikk respons {}", pingable.name(), response);
            return up();
        } catch (Exception e) {
            return down(e);
        }
    }

    private Health up() {
        return Health.up()
                .withDetail(pingable.name(), pingable.pingEndpoint())
                .build();
    }

    private Health down(Exception e) {
        return Health.down()
                .withDetail(pingable.name(), pingable.pingEndpoint())
                .withException(e)
                .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingable=" + pingable + "]";
    }
}
