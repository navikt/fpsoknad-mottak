package no.nav.foreldrepenger.mottak.innsending.dokmot;

import org.springframework.stereotype.Component;

@Component
public class DokmotQueuePinger {

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
