package no.nav.foreldrepenger.mottak.error;

import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Versjon;

public abstract class SøknadEgenskapException extends RuntimeException {
    private final SøknadEgenskap egenskap;

    public SøknadEgenskapException(SøknadEgenskap egenskap) {
        this(null, egenskap, null);
    }

    public SøknadEgenskapException(String msg, SøknadEgenskap egenskap, Throwable cause) {
        super(msg, cause);
        this.egenskap = egenskap;
    }

    public SøknadType getType() {
        return egenskap.getType();
    }

    public Versjon getVersjon() {
        return egenskap.getVersjon();
    }
}
