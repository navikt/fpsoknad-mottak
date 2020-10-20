package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class PDLDødsfall {
    private final LocalDate dødsdato;

    @JsonCreator
    PDLDødsfall(@JsonProperty("doedsdato") LocalDate dødsdato) {
        this.dødsdato = dødsdato;
    }
}
