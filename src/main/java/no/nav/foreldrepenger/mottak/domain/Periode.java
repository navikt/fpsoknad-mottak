package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Period
@Valid
public class Periode {

    private final LocalDate fom;
    private final LocalDate tom;

    @JsonCreator
    public Periode(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }

}
