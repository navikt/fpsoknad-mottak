package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = ANY)
record PDLFamilierelasjon(@JsonProperty("relatertPersonsIdent") String id,
        PDLFamilierelasjon.PDLRelasjonsRolle relatertPersonrolle,
        @JsonProperty("minRolleForPerson") PDLFamilierelasjon.PDLRelasjonsRolle minRolle) {

    static enum PDLRelasjonsRolle {
        BARN,
        MOR,
        FAR,
        MEDMOR
    }
}