package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.http.HttpStatus;

public class UventetPollingStatusFpFordelException extends RuntimeException {
    public UventetPollingStatusFpFordelException(String message) {
        super(message);
    }

    public UventetPollingStatusFpFordelException(Throwable throwable) {
        super(throwable);
    }

    public UventetPollingStatusFpFordelException(HttpStatus httpStatus, String message) {
        super(String.format("[%s] %s", message, httpStatus));
    }
}
