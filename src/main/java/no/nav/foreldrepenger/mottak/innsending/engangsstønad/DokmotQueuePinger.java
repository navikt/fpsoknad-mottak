package no.nav.foreldrepenger.mottak.innsending.engangsstønad;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DokmotQueuePinger {

    private final DokmotConnection connection;

    public DokmotQueuePinger(@Qualifier("dokmotConnection") DokmotConnection connection) {
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
