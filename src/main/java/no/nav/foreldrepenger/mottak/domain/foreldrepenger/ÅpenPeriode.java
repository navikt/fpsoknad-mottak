package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ÅpenPeriode {

    private final LocalDate fom;
    private final LocalDate tom;

    public ÅpenPeriode(LocalDate fom) {
        this(fom, null);
    }

    @JsonCreator
    public ÅpenPeriode(@NotNull @JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }

}
