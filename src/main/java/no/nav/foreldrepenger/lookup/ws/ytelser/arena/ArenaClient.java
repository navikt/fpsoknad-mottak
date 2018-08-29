package no.nav.foreldrepenger.lookup.ws.ytelser.arena;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.lookup.Pingable;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;

public interface ArenaClient extends Pingable {

    List<Ytelse> ytelser(Fødselsnummer fnr, LocalDate from, LocalDate to);
}
