package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLFamilierelasjon;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLFødselsdato;

@Data
class PDLBarn {
    private final Set<PDLFødselsdato> fødselsdato;

    private final Set<PDLFamilierelasjon> familierelasjoner;

    @JsonCreator
    public PDLBarn(@JsonProperty("foedsel") Set<PDLFødselsdato> fødselsdato,
            @JsonProperty("familierelasjoner") Set<PDLFamilierelasjon> familierelasjoner) {
        this.fødselsdato = fødselsdato;
        this.familierelasjoner = familierelasjoner;
    }
}