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
    private final Set<PDLNavn> navn;
    private final Set<PDLKjønn> kjønn;

    private PDLAnnenPart annenPart;

    @JsonCreator
    PDLBarn(@JsonProperty("foedsel") Set<PDLFødsel> fødselsdato,
            @JsonProperty("familierelasjoner") Set<PDLFamilierelasjon> familierelasjoner, @JsonProperty("navn") Set<PDLNavn> navn,
            @JsonProperty("kjoenn") Set<PDLKjønn> kjønn) {
        this.fødselsdato = fødselsdato;
        this.familierelasjoner = familierelasjoner;
        this.navn = navn;
        this.kjønn = kjønn;
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

    PDLBarn withAnnenPart(PDLAnnenPart annenPart) {
        this.annenPart = annenPart;
        return this;
    }

    String getId() {
        return id;
    }
}