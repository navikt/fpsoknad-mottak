package no.nav.foreldrepenger.mottak.error;

public class UnexpectedInputException extends RuntimeException {
    public UnexpectedInputException(String msg) {
        this(msg, (Throwable) null);
    }

    public UnexpectedInputException(String msg, Object... args) {
        this(String.format(msg, args), (Throwable) null);
    }

    public UnexpectedInputException(String msg, Throwable t) {
        super(msg, t);
    }
}
