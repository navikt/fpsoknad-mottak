package no.nav.foreldrepenger.mottak.http.errorhandling;

import java.util.Date;

public class TokenExpiryAwareException extends RuntimeException {

    private final Date expDate;

    public TokenExpiryAwareException(String msg, Date expDate, Throwable cause) {
        super(msg, cause);
        this.expDate = expDate;
    }

    public Date getExpDate() {
        return expDate;
    }
}
