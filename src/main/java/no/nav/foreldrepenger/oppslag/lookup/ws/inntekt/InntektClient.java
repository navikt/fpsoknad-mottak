package no.nav.foreldrepenger.oppslag.lookup.ws.inntekt;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fødselsnummer;

import java.time.LocalDate;
import java.util.List;

public interface InntektClient {

    void ping();

    List<Inntekt> incomeForPeriod(Fødselsnummer fnr, LocalDate from, LocalDate to);
}
