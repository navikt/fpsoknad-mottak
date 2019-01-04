package no.nav.foreldrepenger.mottak.http.errorhandling;

import no.nav.foreldrepenger.mottak.util.Versjon;

public abstract class VersionException extends RuntimeException {

    private final Versjon versjon;

    public VersionException(Versjon versjon) {
        this(null, versjon);
    }

    public VersionException(String msg) {
        this(msg, null);
    }

    public VersionException(String msg, Versjon versjon) {
        this(msg, versjon, null);
    }

    public VersionException(String msg, Versjon versjon, Throwable cause) {
        super(msg, cause);
        this.versjon = versjon;
    }

    public Versjon getVersjon() {
        return versjon;
    }
}
