package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class Refusjon {
    private final Double beløpPrMåned;
    private final LocalDate opphørsDato;
    private final List<RefusjonsEndring> refusjonsEndringer;
}
