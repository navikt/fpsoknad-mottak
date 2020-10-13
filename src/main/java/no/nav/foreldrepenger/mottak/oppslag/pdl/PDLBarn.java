package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLFamilierelasjon.PDLRelasjonsRolle;

@Data
class PDLBarn {
    private final Set<PDLFødsel> fødselsdato;

    private final Set<PDLFamilierelasjon> familierelasjoner;
    private String id;
    private PDLAnnenForelder annenForelder;

    @JsonCreator
    PDLBarn(@JsonProperty("foedsel") Set<PDLFødsel> fødselsdato,
            @JsonProperty("familierelasjoner") Set<PDLFamilierelasjon> familierelasjoner) {
        this.fødselsdato = fødselsdato;
        this.familierelasjoner = familierelasjoner;
    }

    String medmor() {
        return idForRolle(PDLRelasjonsRolle.MEDMOR);
    }

    String far() {
        return idForRolle(PDLRelasjonsRolle.FAR);
    }

    String mor() {
        return idForRolle(PDLRelasjonsRolle.MOR);
    }

    private String idForRolle(PDLRelasjonsRolle rolle) {
        return familierelasjoner.stream()
                .filter(r -> r.getRelatertPersonrolle().equals(rolle))
                .findFirst()
                .map(p -> p.getId())
                .orElse(null);
    }

    PDLBarn withId(String id) {
        this.id = id;
        return this;
    }

    PDLBarn withAnnenForelder(PDLAnnenForelder annenForelder) {
        this.annenForelder = annenForelder;
        return this;
    }

    String getId() {
        return id;
    }
}