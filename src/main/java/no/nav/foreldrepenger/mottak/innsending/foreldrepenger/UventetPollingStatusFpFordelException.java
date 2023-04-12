package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.http.HttpStatusCode;

public class UventetPollingStatusFpFordelException extends RuntimeException {
    public UventetPollingStatusFpFordelException(String message) {
        super(message);
    }

    public UventetPollingStatusFpFordelException(Throwable throwable) {
        super(throwable);
    }

    public UventetPollingStatusFpFordelException(HttpStatusCode httpStatus, String message) {
        super(String.format("[%s] %s", httpStatus.value(), message));
    }
}
