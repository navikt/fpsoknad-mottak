package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class PDLPerson {
    private final Set<PDLNavn> navn;
    private final Set<PDLKjønn> kjønn;
    private final Set<PDLStatsborgerskap> statsborgerskap;
    private final Set<PDLFødsel> fødselsdato;
    private final Set<PDLFamilierelasjon> familierelasjoner;
    private final List<PDLSivilstand> sivilstand;
    private String id;

    @JsonCreator
    PDLPerson(@JsonProperty("navn") Set<PDLNavn> navn,
            @JsonProperty("kjoenn") Set<PDLKjønn> kjønn,
            @JsonProperty("statsborgerskap") Set<PDLStatsborgerskap> statsborgerskap,
            @JsonProperty("foedsel") Set<PDLFødsel> fødselsdato,
            @JsonProperty("familierelasjoner") Set<PDLFamilierelasjon> familierelasjoner,
            @JsonProperty("sivilstand") List<PDLSivilstand> sivilstand) {
        this.navn = navn;
        this.kjønn = kjønn;
        this.statsborgerskap = statsborgerskap;
        this.fødselsdato = fødselsdato;
        this.familierelasjoner = familierelasjoner;
        this.sivilstand = sivilstand;

    }

    PDLPerson withId(String id) {
        this.id = id;
        return this;
    }
}
