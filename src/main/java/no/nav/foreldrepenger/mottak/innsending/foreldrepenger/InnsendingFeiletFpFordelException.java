package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.http.HttpStatusCode;

// FEIL HARDT! Innsending av søknad feilet!
public class InnsendingFeiletFpFordelException extends RuntimeException {

    public InnsendingFeiletFpFordelException(Throwable throwable) {
        super(throwable);
    }

    public InnsendingFeiletFpFordelException(String message) {
        super(message);
    }

    public InnsendingFeiletFpFordelException(HttpStatusCode httpStatus, String message) {
        super(String.format("[%s] %s", httpStatus.value(), message));
    }
}
