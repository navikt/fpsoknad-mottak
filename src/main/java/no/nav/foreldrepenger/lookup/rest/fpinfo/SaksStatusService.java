package no.nav.foreldrepenger.lookup.rest.fpinfo;

import java.util.List;

public interface SaksStatusService {

    List<FPInfoSakStatus> hentSaker(String id, String... behandlingstemaer);
}
