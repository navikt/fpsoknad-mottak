package no.nav.foreldrepenger.mottak.http.errorhandling;

public class SendException extends RuntimeException {

    public SendException(String type, Exception e) {
        super(type, e);
    }
}
