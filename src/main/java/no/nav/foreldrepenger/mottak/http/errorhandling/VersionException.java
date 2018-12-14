package no.nav.foreldrepenger.mottak.http.errorhandling;

import no.nav.foreldrepenger.mottak.util.Versjon;

public abstract class VersionException extends RuntimeException {

    private final Versjon v;

    public VersionException(Versjon v) {
        this(null, v);
    }

    public VersionException(String msg, Versjon v) {
        super(msg);
        this.v = v;
    }
}
