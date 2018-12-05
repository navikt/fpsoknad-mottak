package no.nav.foreldrepenger.mottak.innsending.engangsstønad;

public class DokmotQueueUnavailableException extends RuntimeException {

    private final DokmotQueueConfig config;

    public DokmotQueueUnavailableException(Exception e, DokmotQueueConfig config) {
        this.config = config;
    }

    public DokmotQueueConfig getConfig() {
        return config;
    }
}
