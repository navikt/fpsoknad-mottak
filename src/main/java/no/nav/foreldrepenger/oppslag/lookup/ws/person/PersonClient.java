package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Person;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.ID;

public interface PersonClient {

    Person hentPersonInfo(ID id);

    void ping();

}
