package no.nav.foreldrepenger.lookup.ws.person;

public interface PersonClient {

    Person hentPersonInfo(ID id);

    void ping();

}
