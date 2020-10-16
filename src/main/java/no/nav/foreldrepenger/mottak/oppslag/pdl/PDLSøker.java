package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class PDLSøker {
    private final Set<PDLNavn> navn;
    private final Set<PDLKjønn> kjønn;
    private final Set<PDLStatsborgerskap> statsborgerskap;
    private final Set<PDLFødsel> fødselsdato;
    private final Set<PDLFamilierelasjon> familierelasjoner;
    private final Set<PDLSivilstand> sivilstand;
    private String id;

    @JsonCreator
    PDLSøker(@JsonProperty("navn") Set<PDLNavn> navn,
            @JsonProperty("kjoenn") Set<PDLKjønn> kjønn,
            @JsonProperty("statsborgerskap") Set<PDLStatsborgerskap> statsborgerskap,
            @JsonProperty("foedsel") Set<PDLFødsel> fødselsdato,
            @JsonProperty("familierelasjoner") Set<PDLFamilierelasjon> familierelasjoner,
            @JsonProperty("sivilstand") Set<PDLSivilstand> sivilstand) {
        this.navn = navn;
        this.kjønn = kjønn;
        this.statsborgerskap = statsborgerskap;
        this.fødselsdato = fødselsdato;
        this.familierelasjoner = familierelasjoner;
        this.sivilstand = sivilstand;

    }

    PDLSøker withId(String id) {
        this.id = id;
        return this;
    }
}
