package no.nav.foreldrepenger.oppslag.person;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.oppslag.domain.*;

import java.time.LocalDate;

public class PersonClientStub implements PersonClient {
    @Override
    public Person hentPersonInfo(ID id) {
        Navn navn = new Navn("Jan", "H.", "Johansen");
        return new Person(id, CountryCode.NO, Kjonn.valueOf("M"), navn,
            "nynoregsk", new Bankkonto("1234567890", "Pæng r'us"),
            LocalDate.now().minusYears(20));
    }

    @Override
    public void ping() { }
}
