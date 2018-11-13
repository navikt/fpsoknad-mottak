package no.nav.foreldrepenger.errorhandling;

import java.util.Date;

public class TokenExpiredException extends UnauthenticatedException {

    public TokenExpiredException(Date expDate, Throwable cause) {
        super(expDate, cause);
    }

}
