package no.nav.foreldrepenger.lookup.ws.ytelser.arena;

import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;

import java.time.LocalDate;
import java.util.List;

public interface ArenaClient {

    void ping();

    List<Ytelse> ytelser(Fødselsnummer fnr, LocalDate from, LocalDate to);
}
