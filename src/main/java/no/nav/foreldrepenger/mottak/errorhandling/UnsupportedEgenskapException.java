package no.nav.foreldrepenger.mottak.errorhandling;

import java.util.List;

import no.nav.foreldrepenger.mottak.innsending.mappers.Mappable;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public class UnsupportedEgenskapException extends SøknadEgenskapException {

    public UnsupportedEgenskapException(SøknadEgenskap egenskap) {
        this((String) null, egenskap);
    }

    public UnsupportedEgenskapException(String msg, SøknadEgenskap egenskap) {
        this(msg, egenskap, null);
    }

    public UnsupportedEgenskapException(List<? extends Mappable> mappables, SøknadEgenskap egenskap) {
        this(mappables.toString(), egenskap);
    }

    public UnsupportedEgenskapException(String msg, SøknadEgenskap egenskap, Throwable cause) {
        super(msg, egenskap, cause);
    }

}
