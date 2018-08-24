package no.nav.foreldrepenger.oppslag.lookup.rest.fpinfo;

import java.util.List;

import no.nav.foreldrepenger.oppslag.lookup.ws.aktor.AktorId;

public interface FPInfoSaksStatusService {
    List<FPInfoSakStatus> hentSaker(AktorId id);

    Vedtak hentVedtak(String behandlingsId);

    BehandlingsStatus hentBehandlingsStatus(String behandlingsId);
}
