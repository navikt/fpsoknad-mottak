package no.nav.foreldrepenger.mottak.fpfordel;

import no.nav.foreldrepenger.mottak.http.RemoteUnavailableException;

public class FPFordelUnavailableException extends RemoteUnavailableException {

    public FPFordelUnavailableException(Throwable t) {
        super(t);
    }

}
