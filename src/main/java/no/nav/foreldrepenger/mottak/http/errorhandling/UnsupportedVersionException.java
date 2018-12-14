package no.nav.foreldrepenger.mottak.http.errorhandling;

import no.nav.foreldrepenger.mottak.util.Versjon;

public class UnsupportedVersionException extends VersionException {

    public UnsupportedVersionException(Versjon v) {
        super(v);
    }
}
