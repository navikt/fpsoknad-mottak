package no.nav.foreldrepenger.mottak.domain;

import javax.validation.Valid;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;

public interface SøknadSender {

    Kvittering sendSøknad(Søknad søknad, AktorId aktorId);

    Kvittering sendEttersending(@Valid Ettersending ettersending, AktorId aktørId);

}
