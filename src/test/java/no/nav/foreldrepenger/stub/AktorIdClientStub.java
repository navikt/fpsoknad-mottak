package no.nav.foreldrepenger.stub;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AktorIdClientStub implements AktorIdClient {

    private static final Logger LOG = LoggerFactory.getLogger(AktorIdClientStub.class);

    public AktorId aktorIdForFnr(Fødselsnummer fnr) {
        return new AktorId("Michael learns to rock");
    }

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

}
