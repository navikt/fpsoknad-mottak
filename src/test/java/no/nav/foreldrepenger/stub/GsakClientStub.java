package no.nav.foreldrepenger.stub;

import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
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
    public List<Ytelse> casesFor(Fødselsnummer fnr) {
        return Arrays.asList(
            new Ytelse("foreldrepenger", "ukjent", LocalDate.of(2018, 7, 21)),
            new Ytelse("sykepenger", "ukjent", LocalDate.of(2017, 4, 22))
        );
    }
}
