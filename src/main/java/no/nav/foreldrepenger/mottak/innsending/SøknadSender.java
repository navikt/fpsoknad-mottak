package no.nav.foreldrepenger.mottak.innsending;

import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.http.Pingable;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;

public interface SøknadSender extends Pingable {

    Kvittering søk(Søknad søknad, Person søker, SøknadEgenskap egenskap);

    Kvittering ettersend(Ettersending ettersending, Person søker, SøknadEgenskap egenskap);

    Kvittering endreSøknad(Endringssøknad endringsøknad, Person søker, SøknadEgenskap egenskap);

}
