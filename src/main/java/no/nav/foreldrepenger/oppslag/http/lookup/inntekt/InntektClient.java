package no.nav.foreldrepenger.oppslag.http.lookup.inntekt;

import no.nav.foreldrepenger.oppslag.http.lookup.person.Fodselsnummer;

import java.time.LocalDate;
import java.util.List;

public interface InntektClient {

    void ping();

    List<Inntekt> incomeForPeriod(Fodselsnummer fnr, LocalDate from, LocalDate to);
}
