package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.lookup.ws.inntekt.Inntekt;
import no.nav.foreldrepenger.oppslag.lookup.ws.inntekt.InntektClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InntektClientStub implements InntektClient {

    private static final Logger LOG = LoggerFactory.getLogger(InntektClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public List<Inntekt> incomeForPeriod(Fødselsnummer fnr, LocalDate from, LocalDate to) {
        return new ArrayList<>();
    }
}
