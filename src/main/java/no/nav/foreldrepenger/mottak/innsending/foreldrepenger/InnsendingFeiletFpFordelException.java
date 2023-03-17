package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.http.HttpStatus;

// FEIL HARDT! Innsending av søknad feilet!
public class InnsendingFeiletFpFordelException extends RuntimeException {

    public InnsendingFeiletFpFordelException(Throwable throwable) {
        super(throwable);
    }

    public InnsendingFeiletFpFordelException(String message) {
        super(message);
    }

    public InnsendingFeiletFpFordelException(HttpStatus httpStatus, String message) {
        super(String.format("[%s] %s", httpStatus, message));
    }
}
