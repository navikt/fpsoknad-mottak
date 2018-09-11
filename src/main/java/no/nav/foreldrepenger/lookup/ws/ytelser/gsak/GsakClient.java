package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import no.nav.foreldrepenger.lookup.Pingable;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;

import java.util.List;

public interface GsakClient extends Pingable {

    List<Ytelse> casesFor(Fødselsnummer fnr);
}
