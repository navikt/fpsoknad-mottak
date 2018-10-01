package no.nav.foreldrepenger.mottak.http;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsyn.Behandling;

public interface Innsyn {
    List<Sak> hentSaker(AktorId aktørId);

    List<Sak> hentSaker(String aktørId);

    Behandling hentBehandling(String behandlingId);

    Søknad hentSøknad(String behandlingsId);

}
