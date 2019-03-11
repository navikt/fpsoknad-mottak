package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DelvisFraværsPeriode {

    private final double timer;
    private final LocalDate dato;

}
