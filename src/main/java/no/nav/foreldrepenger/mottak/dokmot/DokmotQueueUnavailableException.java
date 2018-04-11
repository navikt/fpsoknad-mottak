package no.nav.foreldrepenger.mottak.dokmot;

public class DokmotQueueUnavailableException extends RuntimeException {

    private final DokmotQueueConfig config;

    public DokmotQueueUnavailableException(Exception e, DokmotQueueConfig config) {
        super(e);
        this.config = config;
    }

    public DokmotQueueConfig getConfig() {
        return config;
    }
}
