package no.nav.foreldrepenger.mottak.http;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.Behandling;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.Sak;

public interface Innsyn {
    List<Sak> hentSaker(AktorId aktørId);

    List<Sak> hentSaker(String aktørId);

    Behandling hentBehandling(String behandlingId);

    Søknad hentSøknad(String behandlingsId);

}
