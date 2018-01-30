package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Periode
public class LukketPeriode {

    @NotNull
    @Past
    private final LocalDate fom;
    @NotNull
    private final LocalDate tom;

    public LukketPeriode() {
        this(LocalDate.now().minusYears(1), LocalDate.now());
    }

    @JsonCreator
    public LukketPeriode(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }

    public boolean overlapperPeriode(LukketPeriode other) {
        return fom.isBefore(other.getTom()) && other.getFom().isBefore(fom);
    }

}
