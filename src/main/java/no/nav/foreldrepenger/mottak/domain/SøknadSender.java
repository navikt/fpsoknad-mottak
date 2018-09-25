package no.nav.foreldrepenger.mottak.domain;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;

public interface SøknadSender {

    Kvittering send(Søknad søknad, Person søker);

    Kvittering send(Ettersending ettersending, Person søker);

    Kvittering send(Endringssøknad endringsøknad, Person søker);

}
