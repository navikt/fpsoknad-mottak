package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

@Component
public class DokmotQueuePinger {

    private final Counter dokmotSuccess = Metrics.counter("dokmot.ping", "response", "success");
    private final Counter dokmotFailure = Metrics.counter("dokmot.ping", "response", "failure");

    private static final Logger LOG = LoggerFactory.getLogger(DokmotQueuePinger.class);

    private final DokmotConnection connection;

    @Inject
    public DokmotQueuePinger(DokmotConnection connection) {
        this.connection = connection;
    }

    public void ping() {
        try {
            connection.ping();
            dokmotSuccess.increment();
        } catch (JMSException e) {
            LOG.warn("Unable to ping queue at {}", connection.getQueueConfig());
            dokmotFailure.increment();
            throw new DokmotQueueUnavailableException(e);
        }
    }

    public DokmotQueueConfig getQueueConfig() {
        return connection.getQueueConfig();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "]";
    }

}
