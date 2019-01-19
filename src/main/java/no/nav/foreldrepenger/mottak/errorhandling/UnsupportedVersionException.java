package no.nav.foreldrepenger.mottak.errorhandling;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskaper;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class UnsupportedVersionException extends VersionException {

    public UnsupportedVersionException(SøknadEgenskaper egenskaper) {
        this(null, egenskaper);
    }

    public UnsupportedVersionException(String msg, SøknadEgenskaper egenskaper) {
        this(null, egenskaper.getVersjon(), egenskaper.getType(), null);
    }

    public UnsupportedVersionException(Versjon versjon) {
        this(null, versjon, null, null);
    }

    public UnsupportedVersionException(String msg) {
        this(msg, null, null, null);
    }

    public UnsupportedVersionException(String msg, Versjon versjon) {
        this(msg, versjon, null, null);
    }

    public UnsupportedVersionException(Versjon versjon, SøknadType type) {
        this(null, versjon, type, null);
    }

    public UnsupportedVersionException(Throwable cause) {
        this(null, null, null, cause);
    }

    public UnsupportedVersionException(String msg, Versjon versjon, SøknadType type, Throwable cause) {
        super(msg, versjon, type, cause);
    }

}
