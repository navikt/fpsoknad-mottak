package no.nav.foreldrepenger.oppslag.http.lookup.aktor;

import no.nav.foreldrepenger.oppslag.http.lookup.person.Fodselsnummer;

public interface AktorIdClient {

    AktorId aktorIdForFnr(Fodselsnummer fnr);

    void ping();

}
