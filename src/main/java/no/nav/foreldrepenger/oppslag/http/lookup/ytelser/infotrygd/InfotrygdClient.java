package no.nav.foreldrepenger.oppslag.http.lookup.ytelser.infotrygd;

import no.nav.foreldrepenger.oppslag.http.lookup.person.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.Ytelse;

import java.time.LocalDate;
import java.util.List;

public interface InfotrygdClient {

    void ping();

    List<Ytelse> casesFor(Fodselsnummer fnr, LocalDate from, LocalDate to);
}
