package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import java.util.List;

import no.nav.foreldrepenger.lookup.Pingable;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

public interface ArbeidsforholdClient extends Pingable {

    List<Arbeidsforhold> arbeidsforhold(Fødselsnummer fnr);
}
