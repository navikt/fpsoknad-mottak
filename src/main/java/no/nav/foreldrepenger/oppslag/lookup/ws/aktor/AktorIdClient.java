package no.nav.foreldrepenger.oppslag.lookup.ws.aktor;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;

public interface AktorIdClient {

    AktorId aktorIdForFnr(Fodselsnummer fnr);

    void ping();

}
