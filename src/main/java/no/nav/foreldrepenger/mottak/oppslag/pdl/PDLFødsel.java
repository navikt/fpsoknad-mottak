package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class PDLFødsel {
    private final LocalDate fødselsdato;

    @JsonCreator
    PDLFødsel(@JsonProperty("foedselsdato") LocalDate fødselsdato) {
        this.fødselsdato = fødselsdato;
    }
}