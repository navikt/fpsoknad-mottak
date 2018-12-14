package no.nav.foreldrepenger.mottak.domain;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface VersjonerbarSøknadSender extends SøknadSender {

    Kvittering send(Endringssøknad endringsSøknad, Person søker, Versjon versjon);

    Kvittering send(Søknad søknad, Person søker, Versjon versjon);

}
