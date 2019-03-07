package no.nav.foreldrepenger.mottak.errorhandling;

import no.nav.foreldrepenger.mottak.util.Versjon;

public class UnsupportedVersionException extends RuntimeException {

    private final Versjon versjon;

    public UnsupportedVersionException(Versjon versjon) {
        this.versjon = versjon;
    }

}
