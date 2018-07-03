package no.nav.foreldrepenger.oppslag.stub;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Collections.singletonList;
import static no.nav.foreldrepenger.oppslag.lookup.ws.person.Kjønn.M;

public class PersonClientStub implements PersonClient {

    private static final Logger LOG = LoggerFactory.getLogger(PersonClientStub.class);

    @Override
    public Person hentPersonInfo(ID id) {
        Navn navn = new Navn("Skjegg", "Stub", "Sveen");
        return new Person(id, CountryCode.NO, Kjønn.valueOf("M"), navn,
            "NN", new Bankkonto("1234567890", "Stub NOR"),
            now().minusYears(20), barn(id.getFnr()));
    }

    @Override
    public void ping() {
        LOG.info("PONG");

    }

    private List<Barn> barn(Fodselsnummer fnrMor) {
        Barn barn = new Barn(fnrMor,
            new Fodselsnummer("01011812345"),
            now().minusYears(1),
            new Navn("Mo", null, "Sveen"),
            M,
            null);

        return singletonList(barn);
    }
}
