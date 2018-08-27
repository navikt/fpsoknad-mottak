package no.nav.foreldrepenger.lookup.rest.fpinfo;

import java.util.List;

public interface SaksStatusService {

    Vedtak hentVedtak(String behandlingsId);

    BehandlingsStatus hentBehandlingsStatus(String behandlingsId);

    List<FPInfoSakStatus> hentSaker(String id, FPInfoFagsakYtelseType... typer);
}
