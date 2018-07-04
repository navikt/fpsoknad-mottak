package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold.ArbeidsforholdClient;
import no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fødselsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class ArbeidsforholdClientStub implements ArbeidsforholdClient {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public List<Arbeidsforhold> arbeidsforhold(Fødselsnummer fnr) {
        Arbeidsforhold arbeidsforhold1 = new Arbeidsforhold("0123456789", "orgnummer",
            69d, now().minusYears(1), empty());
        Arbeidsforhold arbeidsforhold2 = new Arbeidsforhold("999999999", "orgnummer",
            100d, now().minusYears(2), of(now().minusYears(1)));
        return Arrays.asList(arbeidsforhold1, arbeidsforhold2);
    }
}
