package no.nav.foreldrepenger.mottak.http.errorhandling;

public class UnauthenticatedException extends RuntimeException {

    public UnauthenticatedException(Throwable cause) {
        this(null, null);
    }

    public UnauthenticatedException(String msg) {
        this(msg, null);
    }

    public UnauthenticatedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
