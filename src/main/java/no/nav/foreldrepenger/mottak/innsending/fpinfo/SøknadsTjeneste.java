package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;

public interface SøknadsTjeneste {
    List<FPInfoSakStatus> hentSaker(AktorId aktørId);

    List<FPInfoSakStatus> hentSaker(String aktørId);

    Behandling hentBehandling(String behandlingId);

    Søknad hentSøknad(String behandlingsId);

}
