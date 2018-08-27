package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

import java.util.List;

public interface ArbeidsforholdClient {

    void ping();

    List<Arbeidsforhold> arbeidsforhold(Fødselsnummer fnr);
}
