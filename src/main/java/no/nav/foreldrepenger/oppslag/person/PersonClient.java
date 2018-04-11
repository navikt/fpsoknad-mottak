package no.nav.foreldrepenger.oppslag.person;

import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Person;

public interface PersonClient {

    Person hentPersonInfo(ID id);

    void ping();

}
