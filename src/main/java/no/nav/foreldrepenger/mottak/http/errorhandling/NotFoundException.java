package no.nav.foreldrepenger.mottak.http.errorhandling;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public NotFoundException(String msg) {
        this(msg, null);
    }

    public NotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
