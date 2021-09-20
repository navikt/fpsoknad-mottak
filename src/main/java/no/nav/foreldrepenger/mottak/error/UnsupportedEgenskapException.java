package no.nav.foreldrepenger.mottak.error;

import static java.util.stream.Collectors.joining;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.util.List;

import no.nav.foreldrepenger.common.innsending.mappers.Mappable;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;

public class UnsupportedEgenskapException extends SøknadEgenskapException {
    public UnsupportedEgenskapException(SøknadEgenskap egenskap) {
        this((String) null, egenskap);
    }

    public UnsupportedEgenskapException(String msg, SøknadEgenskap egenskap) {
        this(msg, egenskap, null);
    }

    public UnsupportedEgenskapException(List<? extends Mappable> mappables, SøknadEgenskap egenskap) {
        this("Ingen egenskap " + egenskap + " blant " + navn(mappables), null);
    }

    public UnsupportedEgenskapException(String msg, SøknadEgenskap egenskap, Throwable cause) {
        super(msg, egenskap, cause);
    }

    private static String navn(List<? extends Mappable> mappables) {
        return safeStream(mappables)
                .map(c -> c.getClass().getSimpleName() + " (" + c.mapperEgenskaper() + ")")
                .collect(joining(","));
    }
}
