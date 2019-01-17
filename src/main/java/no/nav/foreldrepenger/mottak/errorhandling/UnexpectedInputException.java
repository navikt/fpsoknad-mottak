package no.nav.foreldrepenger.mottak.errorhandling;

public class UnexpectedInputException extends RuntimeException {

    public UnexpectedInputException(String msg) {
        super(msg);
    }

}
