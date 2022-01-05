package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.http.HttpStatus;

public class UventetFpFordelResponseException extends RuntimeException {
    public UventetFpFordelResponseException(HttpStatus statusCode) {
        super("Uventet response fra fpfordel. Status " + statusCode);
    }
}
