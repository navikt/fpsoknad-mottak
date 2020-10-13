package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class PDLAnnenForelder {
    private final Set<PDLNavn> navn;
    private final Set<PDLFødsel> fødselsdato;
    private final Set<PDLKjønn> kjønn;
    private String id;

    @JsonCreator
    public PDLAnnenForelder(@JsonProperty("navn") Set<PDLNavn> navn, @JsonProperty("foedsel") Set<PDLFødsel> fødselsdato,
            @JsonProperty("kjoenn") Set<PDLKjønn> kjønn) {
        this.navn = navn;
        this.fødselsdato = fødselsdato;
        this.kjønn = kjønn;
    }

    PDLAnnenForelder withId(String id) {
        this.id = id;
        return this;
    }
}