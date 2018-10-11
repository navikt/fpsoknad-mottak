package no.nav.foreldrepenger.stub;

import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import no.nav.foreldrepenger.lookup.ws.ytelser.gsak.GsakClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class GsakClientStub implements GsakClient {

    private static final Logger LOG = LoggerFactory.getLogger(GsakClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public List<Sak> casesFor(Fødselsnummer fnr) {
        return Arrays.asList(
            new Sak("sak1", "typen", "system", "fsid1",
                "status", LocalDate.of(2018,9,19)),
            new Sak("sak2", "typen", "system", "fsid2",
                "status", LocalDate.of(2018,9,18)),
            new Sak("sak3", "typen", "system", "fsid3",
                "status", LocalDate.of(2018,9,17))
        );
    }
}
