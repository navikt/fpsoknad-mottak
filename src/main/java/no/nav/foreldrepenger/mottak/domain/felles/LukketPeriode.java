package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Periode;

@Data
@Periode
@JsonPropertyOrder({ "fom", "tom" })
public class LukketPeriode {

    @NotNull
    private final LocalDate fom;
    @NotNull
    private final LocalDate tom;

    @JsonCreator
    public LukketPeriode(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }

    @JsonIgnore
    public boolean isWithinPeriod(LocalDate dato) {
        return dato.isAfter(fom.minusDays(1)) && dato.isBefore(tom.plusDays(1));
    }

}
