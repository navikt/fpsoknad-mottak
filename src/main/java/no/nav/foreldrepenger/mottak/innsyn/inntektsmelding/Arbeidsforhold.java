package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;

@Data
public class Arbeidsforhold {
    private final String id;
    private final LocalDate førsteFraværsdag;
    private final Inntekt inntekt;
    private final List<LukketPeriode> avtaltFeriePerioder;
    private final List<UtsettelsesPeriode> utsettelsesPerioder;
    private final List<GraderingsPeriode> graderingsPerioder;

}
