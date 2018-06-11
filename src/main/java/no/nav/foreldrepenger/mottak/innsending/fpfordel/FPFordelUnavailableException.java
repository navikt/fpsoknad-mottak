package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import no.nav.foreldrepenger.mottak.http.RemoteUnavailableException;

public class FPFordelUnavailableException extends RemoteUnavailableException {

    public FPFordelUnavailableException(Throwable t) {
        this(null, t);
    }

    public FPFordelUnavailableException(String msg) {
        this(msg, null);
    }

    public FPFordelUnavailableException(String msg, Throwable t) {
        super(msg, t);
    }

}
