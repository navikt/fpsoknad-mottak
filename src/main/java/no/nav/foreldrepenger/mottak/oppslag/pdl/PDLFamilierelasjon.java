package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonProperty;

record PDLFamilierelasjon(@JsonProperty("relatertPersonsIdent") String id,
        PDLFamilierelasjon.PDLRelasjonsRolle relatertPersonsrolle,
        @JsonProperty("minRolleForPerson") PDLFamilierelasjon.PDLRelasjonsRolle minRolle) {

    static enum PDLRelasjonsRolle {
        BARN,
        MOR,
        FAR,
        MEDMOR
    }
}