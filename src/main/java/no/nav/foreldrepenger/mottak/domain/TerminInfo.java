package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TerminInfo {
    private final LocalDate terminDato;
    private final LocalDate utstedtDato;

    @JsonCreator
    public TerminInfo(@JsonProperty("terminDato") LocalDate terminDato,
            @JsonProperty("utstedtDato") LocalDate utstedtDato) {
        this.terminDato = terminDato;
        this.utstedtDato = utstedtDato;
    }
}
