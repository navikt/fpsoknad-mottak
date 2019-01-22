package no.nav.foreldrepenger.mottak.innsending.pdf;

public class PDFException extends RuntimeException {

    public PDFException(String msg) {
        this(msg, null);
    }

    public PDFException(String msg, Throwable t) {
        super(msg, t);
    }

}
