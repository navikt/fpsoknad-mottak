package no.nav.foreldrepenger.mottak.innsending.engangsstønad;

import no.nav.foreldrepenger.mottak.http.errorhandling.RemoteUnavailableException;

public class DokmotQueueUnavailableException extends RemoteUnavailableException {

    private final DokmotQueueConfig config;

    public DokmotQueueUnavailableException(Exception e, DokmotQueueConfig config) {
        super(e);
        this.config = config;
    }

    public DokmotQueueConfig getConfig() {
        return config;
    }
}
