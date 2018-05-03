package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.http.lookup.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.http.lookup.aktor.AktorId;
import no.nav.foreldrepenger.oppslag.http.lookup.person.Fodselsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AktorIdClientStub implements AktorIdClient {

    private static final Logger LOG = LoggerFactory.getLogger(AktorIdClientStub.class);

    public AktorId aktorIdForFnr(Fodselsnummer fnr) {
        return new AktorId("Michael learns to rock");
    }

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

}
