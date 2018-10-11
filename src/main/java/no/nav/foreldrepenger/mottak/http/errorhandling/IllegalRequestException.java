package no.nav.foreldrepenger.mottak.http.errorhandling;

public class IllegalRequestException extends RuntimeException {

    public IllegalRequestException(String msg, Exception cause) {
        super(msg, cause);
    }

}
