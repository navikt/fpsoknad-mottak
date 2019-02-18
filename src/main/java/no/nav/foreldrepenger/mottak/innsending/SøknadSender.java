package no.nav.foreldrepenger.mottak.innsending;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public interface SøknadSender {
    String FPFORDEL_SENDER = "fpfordel";

    Kvittering send(Søknad søknad, Person søker, SøknadEgenskap egenskap);

    Kvittering send(Ettersending ettersending, Person søker, SøknadEgenskap egenskap);

    Kvittering send(Endringssøknad endringsøknad, Person søker, SøknadEgenskap egenskap);
}
