package no.nav.foreldrepenger.mottak.dokmot;

public class DokmotQueueUnavailableException extends RuntimeException {

    public DokmotQueueUnavailableException(Exception e) {
        super(e);
    }
}
