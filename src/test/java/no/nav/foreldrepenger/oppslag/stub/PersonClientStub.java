package no.nav.foreldrepenger.oppslag.stub;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static java.util.Collections.emptyList;

public class PersonClientStub implements PersonClient {

    private static final Logger LOG = LoggerFactory.getLogger(PersonClientStub.class);

    @Override
    public Person hentPersonInfo(ID id) {
        Navn navn = new Navn("Skjegg", "Stub", "Sveen");
        return new Person(id, CountryCode.NO, Kjonn.valueOf("M"), navn,
            "NN", new Bankkonto("1234567890", "Stub NOR"),
            LocalDate.now().minusYears(20), emptyList());
    }

    @Override
    public void ping() {
        LOG.info("PONG");

    }
}
