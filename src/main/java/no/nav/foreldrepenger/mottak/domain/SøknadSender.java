package no.nav.foreldrepenger.mottak.domain;

import javax.validation.Valid;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;

public interface SøknadSender {

    Kvittering sendSøknad(Søknad søknad, Person søker);

    Kvittering sendEttersending(@Valid Ettersending ettersending, Person søker);

}
