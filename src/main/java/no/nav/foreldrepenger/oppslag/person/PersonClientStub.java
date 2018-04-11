package no.nav.foreldrepenger.oppslag.person;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.oppslag.domain.*;

import java.time.LocalDate;
import java.util.Collections;

public class PersonClientStub implements PersonClient {
    @Override
    public Person hentPersonInfo(ID id) {
        Navn navn = new Navn("Jan", "H.", "Johansen");
        Adresse adresse = new Adresse(CountryCode.NO, "0123", "Oslo",
            "Veien", "42", "A");
        Person person = new Person(id, CountryCode.NO, Kjonn.valueOf("M"), navn,
            adresse, "nynoregsk", LocalDate.now().minusYears(20), Collections.emptyList());
        return person;
    }

    @Override
    public void ping() { }
}
