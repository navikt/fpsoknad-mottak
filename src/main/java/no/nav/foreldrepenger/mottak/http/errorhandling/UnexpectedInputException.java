package no.nav.foreldrepenger.mottak.http.errorhandling;

public class UnexpectedInputException extends RuntimeException {

    public UnexpectedInputException(String msg) {
        super(msg);
    }

}
