package no.nav.foreldrepenger.mottak.http.errorhandling;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

public abstract class VersionException extends RuntimeException {

    private final Versjon versjon;
    private final SøknadType type;

    public VersionException(Versjon versjon) {
        this(null, versjon);
    }

    public VersionException(String msg) {
        this(msg, null);
    }

    public VersionException(String msg, Versjon versjon) {
        this(msg, versjon, null, null);
    }

    public VersionException(String msg, Versjon versjon, SøknadType type, Throwable cause) {
        super(msg, cause);
        this.versjon = versjon;
        this.type = type;
    }

    public SøknadType getType() {
        return type;
    }

    public Versjon getVersjon() {
        return versjon;
    }
}
