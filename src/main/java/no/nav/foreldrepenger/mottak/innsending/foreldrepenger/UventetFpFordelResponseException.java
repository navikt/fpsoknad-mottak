package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.http.HttpStatus;

public class UventetFpFordelResponseException extends RuntimeException {
    public UventetFpFordelResponseException(String message) {
        super(message);
    }

    public UventetFpFordelResponseException(Throwable throwable) {
        super(throwable);
    }

    public UventetFpFordelResponseException(HttpStatus statusCode) {
        super("Uventet response fra fpfordel. Status " + statusCode);
    }

    public UventetFpFordelResponseException(HttpStatus httpStatus, String message) {
        super(String.format("[%s] %s", message, httpStatus));
    }
}
