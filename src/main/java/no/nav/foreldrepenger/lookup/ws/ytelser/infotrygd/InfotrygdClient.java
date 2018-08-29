package no.nav.foreldrepenger.lookup.ws.ytelser.infotrygd;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.lookup.Pingable;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;

public interface InfotrygdClient extends Pingable {

    List<Ytelse> casesFor(Fødselsnummer fnr, LocalDate from, LocalDate to);
}
