package no.nav.foreldrepenger.oppslag.errorhandling;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public ForbiddenException(String msg) {
        this(msg, null);
    }

    public ForbiddenException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
