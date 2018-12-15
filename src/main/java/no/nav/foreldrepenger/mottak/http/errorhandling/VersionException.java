package no.nav.foreldrepenger.mottak.http.errorhandling;

import no.nav.foreldrepenger.mottak.util.Versjon;

public abstract class VersionException extends RuntimeException {

    private final Versjon versjon;

    public VersionException(Versjon versjon) {
        this(null, versjon);
    }

    public VersionException(String msg, Versjon versjon) {
        super(msg);
        this.versjon = versjon;
    }

    public Versjon getVersjon() {
        return versjon;
    }
}
