package no.nav.foreldrepenger.lookup.rest.fpinfo;

import java.util.List;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;

public interface SaksStatusService {

    List<FPInfoSakStatus> hentSaker(AktorId id);

    List<FPInfoSakStatus> hentSaker(String id);
}
