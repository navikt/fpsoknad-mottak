package no.nav.foreldrepenger.oppslag.lookup.ws.aareg;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;

import java.util.List;

public interface AaregClient {

    void ping();

    List<Arbeidsforhold> arbeidsforhold(Fodselsnummer fnr);
}
