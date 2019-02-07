package no.nav.foreldrepenger.mottak.errorhandling;

import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public class UnsupportedEgenskapException extends EgenskapException {

    public UnsupportedEgenskapException(SøknadEgenskap egenskap) {
        this(null, egenskap);
    }

    public UnsupportedEgenskapException(String msg, SøknadEgenskap egenskap) {
        this(msg, egenskap, null);
    }

    public UnsupportedEgenskapException(String msg, SøknadEgenskap egenskap, Throwable cause) {
        super(msg, egenskap, cause);
    }

}
