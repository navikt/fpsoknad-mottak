package no.nav.foreldrepenger.mottak.dokmot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DokmotQueuePinger {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotQueuePinger.class);

    private final DokmotConnection connection;

    public DokmotQueuePinger(DokmotConnection connection) {
        this.connection = connection;
    }

    public void ping() {
        connection.ping();
    }

    public DokmotQueueConfig getQueueConfig() {
        return connection.getQueueConfig();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "]";
    }
}
