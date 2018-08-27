package no.nav.foreldrepenger.lookup.ws.aktor;

import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

public interface AktorIdClient {

    AktorId aktorIdForFnr(Fødselsnummer fnr);

    void ping();

}
