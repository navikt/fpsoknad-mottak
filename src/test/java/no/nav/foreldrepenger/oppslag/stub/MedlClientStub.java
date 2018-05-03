package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.http.lookup.person.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.http.lookup.medl.MedlPeriode;
import no.nav.foreldrepenger.oppslag.http.lookup.medl.MedlClient;
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
    public List<MedlPeriode> medlInfo(Fodselsnummer fnr) {
        return new ArrayList<>();
    }
}
