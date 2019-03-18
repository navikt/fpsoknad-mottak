package no.nav.foreldrepenger.mottak.errorhandling;

public class UnexpectedInputException extends RuntimeException {

    public UnexpectedInputException(String msg) {
        this(msg, null);
    }

    public UnexpectedInputException(String msg, Throwable t) {
        super(msg, t);
    }

}
