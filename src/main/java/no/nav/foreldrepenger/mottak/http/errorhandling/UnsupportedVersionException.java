package no.nav.foreldrepenger.mottak.http.errorhandling;

import no.nav.foreldrepenger.mottak.util.Versjon;

public class UnsupportedVersionException extends VersionException {

    public UnsupportedVersionException(Versjon versjon) {
        this(null, versjon, null);
    }

    public UnsupportedVersionException(Throwable cause) {
        this(null, null, cause);
    }

    public UnsupportedVersionException(String msg) {
        this(msg, null, null);
    }

    public UnsupportedVersionException(String msg, Versjon versjon, Throwable cause) {
        super(msg, versjon, cause);
    }

}
