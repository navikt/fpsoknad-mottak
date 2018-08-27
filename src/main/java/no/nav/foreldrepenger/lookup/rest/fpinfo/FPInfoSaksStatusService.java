package no.nav.foreldrepenger.lookup.rest.fpinfo;

import java.util.List;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;

public interface FPInfoSaksStatusService {
    List<FPInfoSakStatus> hentSaker(AktorId id);

    Vedtak hentVedtak(String behandlingsId);

    BehandlingsStatus hentBehandlingsStatus(String behandlingsId);
}
