package no.nav.foreldrepenger.mottak.http.errorhandling;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;

public class SendException extends RuntimeException {

    public SendException(SøknadType type, Exception e) {
        super(type.name(), e);
    }
}
