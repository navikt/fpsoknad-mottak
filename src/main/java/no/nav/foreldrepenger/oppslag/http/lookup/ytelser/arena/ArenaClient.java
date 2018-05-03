package no.nav.foreldrepenger.oppslag.http.lookup.ytelser.arena;

import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.http.lookup.person.Fodselsnummer;

import java.time.LocalDate;
import java.util.List;

public interface ArenaClient {

    void ping();

    List<Ytelse> ytelser(Fodselsnummer fnr, LocalDate from, LocalDate to);
}
