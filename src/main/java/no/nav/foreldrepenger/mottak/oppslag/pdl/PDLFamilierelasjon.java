package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data class PDLFamilierelasjon {

    private final String id;
    private final PDLFamilierelasjon.PDLRelasjonsRolle relatertPersonrolle;
    private final PDLFamilierelasjon.PDLRelasjonsRolle minRolle;

    @JsonCreator
    public PDLFamilierelasjon(@JsonProperty("relatertPersonsIdent") String id,
            @JsonProperty("relatertPersonsRolle") PDLFamilierelasjon.PDLRelasjonsRolle relatertPersonrolle,
            @JsonProperty("minRolleForPerson") PDLFamilierelasjon.PDLRelasjonsRolle minRolle) {
        this.id = id;
        this.relatertPersonrolle = relatertPersonrolle;
        this.minRolle = minRolle;
    }

    static enum PDLRelasjonsRolle {
        BARN,
        MOR,
        FAR,
        MEDMOR
    }
}