package no.nav.foreldrepenger.mottak.http.errorhandling;

import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(Throwable cause) {
        this(getMostSpecificCause(cause).getMessage(), cause);
    }

    public UnauthorizedException(String msg) {
        this(msg, null);
    }

    public UnauthorizedException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
