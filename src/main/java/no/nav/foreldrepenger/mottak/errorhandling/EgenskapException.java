package no.nav.foreldrepenger.mottak.errorhandling;

import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Versjon;

public abstract class EgenskapException extends RuntimeException {

    private final SøknadEgenskap egenskap;

    public EgenskapException(SøknadEgenskap egenskap) {
        this(null, egenskap, null);
    }

    public EgenskapException(String msg, SøknadEgenskap egenskap, Throwable cause) {
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
