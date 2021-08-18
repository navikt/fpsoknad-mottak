package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.nav.foreldrepenger.mottak.domain.validation.annotations.Periode;


@JsonPropertyOrder({ "fom", "tom" })
@Periode
public record LukketPeriode(LocalDate fom, LocalDate tom) {
    @JsonIgnore
    public boolean isWithinPeriod(LocalDate dato) {
        return dato.isAfter(fom().minusDays(1)) && dato.isBefore(tom().plusDays(1));
    }

}
