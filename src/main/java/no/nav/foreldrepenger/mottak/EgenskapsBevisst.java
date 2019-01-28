package no.nav.foreldrepenger.mottak;

import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public interface EgenskapsBevisst {

    MapperEgenskaper mapperEgenskaper();

    default boolean kanMappe(SøknadEgenskap egenskap) {
        return mapperEgenskaper().kanMappe(egenskap);
    }
}
