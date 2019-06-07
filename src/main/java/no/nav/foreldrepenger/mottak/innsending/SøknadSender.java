package no.nav.foreldrepenger.mottak.innsending;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public interface SøknadSender extends Pingable {

    Kvittering søk(Søknad søknad, Person søker, SøknadEgenskap egenskap);

    Kvittering ettersend(Ettersending ettersending, Person søker, SøknadEgenskap egenskap);

    Kvittering endreSøknad(Endringssøknad endringsøknad, Person søker, SøknadEgenskap egenskap);

}
