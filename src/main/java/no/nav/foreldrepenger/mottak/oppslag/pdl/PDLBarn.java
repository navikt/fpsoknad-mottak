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

    String annenPart(String fnrSøker) {
        return familierelasjoner.stream()
                .filter(r -> r.getMinRolle().equals(PDLRelasjonsRolle.BARN))
                .filter(r -> !r.getId().equals(fnrSøker))
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