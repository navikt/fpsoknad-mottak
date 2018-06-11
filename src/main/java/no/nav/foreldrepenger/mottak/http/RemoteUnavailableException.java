package no.nav.foreldrepenger.mottak.http;

public class RemoteUnavailableException extends RuntimeException {

    public RemoteUnavailableException(Throwable t) {
        this(null, t);
    }

    public RemoteUnavailableException(String msg, Throwable t) {
        super(msg, t);
    }

}
