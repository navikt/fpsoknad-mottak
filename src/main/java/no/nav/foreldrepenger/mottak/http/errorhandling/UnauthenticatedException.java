package no.nav.foreldrepenger.mottak.http.errorhandling;

import java.util.Date;

public class UnauthenticatedException extends TokenExpiryAwareException {

    public UnauthenticatedException(Throwable cause) {
        this(null, null, cause);
    }

    public UnauthenticatedException(String msg) {
        this(msg, null, null);
    }

    public UnauthenticatedException(Date expDate, Throwable cause) {
        this(null, expDate, cause);
    }

    public UnauthenticatedException(String msg, Date expDate) {
        this(msg, expDate, null);
    }

    public UnauthenticatedException(String msg, Date expDate, Throwable cause) {
        super(msg, expDate, cause);
    }
}
