package no.nav.foreldrepenger.lookup.ws.inntekt;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.lookup.Pingable;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

public interface InntektClient extends Pingable {

    List<Inntekt> incomeForPeriod(Fødselsnummer fnr, LocalDate from, LocalDate to);
}
