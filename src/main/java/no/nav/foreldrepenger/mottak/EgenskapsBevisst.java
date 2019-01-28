package no.nav.foreldrepenger.mottak;

import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskaper;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface VersjonsBevisst {

    MapperEgenskaper mapperEgenskaper();

    default boolean kanMappe(SøknadEgenskaper søknadEgenskaper) {
        return mapperEgenskaper().kanMappe(søknadEgenskaper);
    }

    default Versjon versjon() {
        return mapperEgenskaper().getVersjon();
    }
}
