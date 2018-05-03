package no.nav.foreldrepenger.oppslag.http.lookup.aareg;

import no.nav.foreldrepenger.oppslag.http.lookup.person.Fodselsnummer;

import java.util.List;

public interface AaregClient {

    void ping();

    List<Arbeidsforhold> arbeidsforhold(Fodselsnummer fnr);
}
