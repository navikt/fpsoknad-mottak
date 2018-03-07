package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DokmotQueuePinger {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotQueuePinger.class);

    private final DokmotConnection connection;

    @Inject
    public DokmotQueuePinger(DokmotConnection connection) {
        this.connection = connection;
    }

    public void ping() {
        try {
            LOG.info("Pinging queue {}", connection.getQueueConfig());
            connection.getTemplate().getConnectionFactory().createConnection().close();
        } catch (JMSException e) {
            LOG.warn("Unable to ping queue at {}", connection.getQueueConfig());
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
