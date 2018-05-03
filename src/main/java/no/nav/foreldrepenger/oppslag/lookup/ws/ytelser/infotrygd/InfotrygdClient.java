package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.infotrygd;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.Ytelse;

import java.time.LocalDate;
import java.util.List;

public interface InfotrygdClient {

    void ping();

    List<Ytelse> casesFor(Fodselsnummer fnr, LocalDate from, LocalDate to);
}
