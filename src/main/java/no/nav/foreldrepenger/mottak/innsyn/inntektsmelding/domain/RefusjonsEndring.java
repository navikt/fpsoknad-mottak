package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RefusjonsEndring {

    private final LocalDate refusjonsDato;
    private final Double refusjonsBeløp;
}
