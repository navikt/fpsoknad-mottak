package no.nav.foreldrepenger.mottak.error;

import no.nav.foreldrepenger.mottak.util.Versjon;

public class UnsupportedVersionException extends RuntimeException {
    private final Versjon versjon;

    public UnsupportedVersionException(Versjon versjon) {
        this.versjon = versjon;
    }

    public Versjon getVersjon() {
        return versjon;
    }
}
