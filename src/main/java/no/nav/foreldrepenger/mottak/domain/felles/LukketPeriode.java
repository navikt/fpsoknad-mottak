package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.Periode;

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
    public boolean isWithinPeriode(LocalDate day) {
        LocalDate dayBeforeFirst = fom.minusDays(1);
        LocalDate dayAfterLast = tom.plusDays(1);
        return day.isAfter(dayBeforeFirst) && day.isBefore(dayAfterLast);
    }

}
