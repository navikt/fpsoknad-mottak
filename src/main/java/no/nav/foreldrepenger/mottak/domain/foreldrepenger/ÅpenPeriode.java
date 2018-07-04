package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.Periode;

@Data
@Periode
public class ÅpenPeriode {

    private final LocalDate fom;
    private final LocalDate tom;

    @JsonCreator
    public ÅpenPeriode(@NotNull LocalDate fom, LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }

    public ÅpenPeriode(@NotNull LocalDate fom) {
        this(fom, null);
    }
}
