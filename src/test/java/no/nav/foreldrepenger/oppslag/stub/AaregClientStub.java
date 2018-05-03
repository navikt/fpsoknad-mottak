package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.lookup.ws.aareg.AaregClient;
import no.nav.foreldrepenger.oppslag.lookup.ws.aareg.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AaregClientStub implements AaregClient {

    private static final Logger LOG = LoggerFactory.getLogger(AaregClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public List<Arbeidsforhold> arbeidsforhold(Fodselsnummer fnr) {
        return new ArrayList<>();
    }
}
