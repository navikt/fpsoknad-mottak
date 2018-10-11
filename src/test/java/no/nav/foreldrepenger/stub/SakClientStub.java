package no.nav.foreldrepenger.stub;

import no.nav.foreldrepenger.lookup.rest.sak.SakClient;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class SakClientStub implements SakClient {
    @Override
    public List<Sak> sakerFor(AktorId aktor, String oidcToken) {
        return Arrays.asList(
            new Sak("sak1", "typen", "systemet", "fsid1",
                "status", LocalDate.of(2018,9,19)),
            new Sak("sak2", "typen", "systemet", "fsid2",
                "status", LocalDate.of(2018,9,18)),
            new Sak("sak3", "typen", "systemet", "fsid3",
                "status", LocalDate.of(2018,9,17))
        );
    }
}
