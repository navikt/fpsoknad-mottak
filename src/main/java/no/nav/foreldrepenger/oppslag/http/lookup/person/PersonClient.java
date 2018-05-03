package no.nav.foreldrepenger.oppslag.http.lookup.person;

import no.nav.foreldrepenger.oppslag.http.lookup.person.Person;
import no.nav.foreldrepenger.oppslag.http.lookup.person.ID;

public interface PersonClient {

    Person hentPersonInfo(ID id);

    void ping();

}
