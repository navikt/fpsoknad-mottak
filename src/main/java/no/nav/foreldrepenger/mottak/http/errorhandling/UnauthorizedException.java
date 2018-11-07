package no.nav.foreldrepenger.mottak.http.errorhandling;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public UnauthorizedException(String msg) {
        this(msg, null);
    }

    public UnauthorizedException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
