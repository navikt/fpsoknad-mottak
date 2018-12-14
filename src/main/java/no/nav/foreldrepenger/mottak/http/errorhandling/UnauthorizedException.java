package no.nav.foreldrepenger.mottak.http.errorhandling;

import java.util.Date;

public class UnauthorizedException extends TokenExpiryAwareException {

    public UnauthorizedException(Throwable cause) {
        this(null, null, cause);
    }

    public UnauthorizedException(String msg) {
        this(msg, null, null);
    }

    public UnauthorizedException(Date expDate, Throwable cause) {
        this(null, expDate, cause);
    }

    public UnauthorizedException(String msg, Date expDate) {
        this(msg, expDate, null);
    }

    public UnauthorizedException(String msg, Date expDate, Throwable cause) {
        super(msg, expDate, cause);
    }
}
