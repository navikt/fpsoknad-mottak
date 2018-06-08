package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.lookup.ws.aareg.AaregClient;
import no.nav.foreldrepenger.oppslag.lookup.ws.aareg.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;

public class AaregClientStub implements AaregClient {

    private static final Logger LOG = LoggerFactory.getLogger(AaregClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public List<Arbeidsforhold> arbeidsforhold(Fodselsnummer fnr) {
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold("0123456789", "orgnummer", "Kjerrekusk", now().minusYears(1), empty());
        return singletonList(arbeidsforhold);
    }
}
