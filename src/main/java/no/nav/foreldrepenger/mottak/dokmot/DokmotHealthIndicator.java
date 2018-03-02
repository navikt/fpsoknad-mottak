package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class DokmotHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotHealthIndicator.class);

    private final JmsTemplate dokmotTemplate;

    @Inject
    public DokmotHealthIndicator(JmsTemplate dokmotTemplate) {
        this.dokmotTemplate = dokmotTemplate;
    }

    @Override
    public Health health() {
        try {
            dokmotTemplate.getConnectionFactory().createConnection().close();
            return Health.up().build();
        } catch (JMSException e) {
            LOG.warn("Could not verify health of queue", e);
            return Health.down().withException(e).build();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotTemplate + "]";
    }

}
