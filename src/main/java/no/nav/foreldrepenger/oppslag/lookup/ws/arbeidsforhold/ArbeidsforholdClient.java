package no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fødselsnummer;

import java.util.List;

public interface ArbeidsforholdClient {

    void ping();

    List<Arbeidsforhold> arbeidsforhold(Fødselsnummer fnr);
}
