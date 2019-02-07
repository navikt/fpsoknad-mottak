package no.nav.foreldrepenger.mottak;

import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public interface Mappable {

    String DELEGERENDE = "delegerende";

    MapperEgenskaper mapperEgenskaper();

    default boolean kanMappe(SøknadEgenskap søknadEgenskap) {
        return mapperEgenskaper().kanMappe(søknadEgenskap);
    }
}
