package no.nav.foreldrepenger.oppslag.lookup.ws.person;

public interface PersonClient {

    Person hentPersonInfo(ID id);

    void ping();

}
