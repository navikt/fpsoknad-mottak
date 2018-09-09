package no.nav.foreldrepenger.stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

public class AktorIdClientStub implements AktorIdClient {

    private static final Logger LOG = LoggerFactory.getLogger(AktorIdClientStub.class);

    public AktorId aktorIdForFnr(Fødselsnummer fnr) {
        return new AktorId("Michael learns to rock");
    }

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public Fødselsnummer fnrForAktørId(AktorId fnr) {
        return new Fødselsnummer("01010100000");
    }

}
