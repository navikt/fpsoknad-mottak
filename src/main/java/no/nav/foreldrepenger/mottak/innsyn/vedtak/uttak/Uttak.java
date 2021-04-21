package no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak;

import java.time.LocalDate;
import java.util.List;

public record Uttak(LocalDate førsteLovligeUttaksDato, List<UttaksPeriode> uttaksPerioder) {

}
