package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.arena;

import no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fødselsnummer;

import java.time.LocalDate;
import java.util.List;

public interface ArenaClient {

    void ping();

    List<Ytelse> ytelser(Fødselsnummer fnr, LocalDate from, LocalDate to);
}
