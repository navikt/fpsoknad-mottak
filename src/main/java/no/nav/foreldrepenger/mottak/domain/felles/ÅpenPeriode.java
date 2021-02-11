package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

public record ÅpenPeriode(@NotNull LocalDate fom, LocalDate tom) {
    public ÅpenPeriode(LocalDate fom) {
        this(fom, null);
    }

}
