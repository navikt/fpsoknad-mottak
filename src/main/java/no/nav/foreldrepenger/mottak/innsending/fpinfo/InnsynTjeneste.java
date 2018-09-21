package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;

public interface InnsynTjeneste {
    List<SakStatus> hentSaker(AktorId aktørId);

    List<SakStatus> hentSaker(String aktørId);

    Behandling hentBehandling(String behandlingId);

    Søknad hentSøknad(String behandlingsId);

}
