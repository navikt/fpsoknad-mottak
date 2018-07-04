package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.lookup.ws.medl.MedlPeriode;
import no.nav.foreldrepenger.oppslag.lookup.ws.medl.MedlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MedlClientStub implements MedlClient {

    private static final Logger LOG = LoggerFactory.getLogger(MedlClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public List<MedlPeriode> medlInfo(Fødselsnummer fnr) {
        return new ArrayList<>();
    }
}
