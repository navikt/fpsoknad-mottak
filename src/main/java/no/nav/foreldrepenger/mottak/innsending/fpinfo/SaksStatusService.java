package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;

public interface SaksStatusService {

    Vedtak hentVedtak(String behandlingsId);

    Søknad hentSøknad(String behandlingsId);

    BehandlingsStatus hentBehandlingsStatus(String behandlingsId);

    List<FPInfoSakStatus> hentSaker(AktorId id, FPInfoFagsakYtelseType... typer);
}
