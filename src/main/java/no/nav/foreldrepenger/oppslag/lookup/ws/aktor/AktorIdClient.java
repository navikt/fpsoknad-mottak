package no.nav.foreldrepenger.oppslag.lookup.ws.aktor;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fødselsnummer;

public interface AktorIdClient {

    AktorId aktorIdForFnr(Fødselsnummer fnr);

    void ping();

}
