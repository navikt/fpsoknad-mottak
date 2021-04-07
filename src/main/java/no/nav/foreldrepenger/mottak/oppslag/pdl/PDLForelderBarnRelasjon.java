package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonProperty;

record PDLForelderBarnRelasjon(@JsonProperty("relatertPersonsIdent") String id,
        PDLForelderBarnRelasjon.PDLRelasjonsRolle relatertPersonsrolle,
        @JsonProperty("minRolleForPerson") PDLForelderBarnRelasjon.PDLRelasjonsRolle minRolle) {

    static enum PDLRelasjonsRolle {
        BARN,
        MOR,
        FAR,
        MEDMOR
    }
}