package no.nav.foreldrepenger.mottak.innsending.dokmot;

import no.nav.foreldrepenger.mottak.http.RemoteUnavailableException;

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
