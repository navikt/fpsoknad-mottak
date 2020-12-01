package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonProperty;

record PDLFamilierelasjon(@JsonProperty("relatertPersonsIdent") String id,
        PDLFamilierelasjon.PDLRelasjonsRolle relatertPersonsrolle,
        PDLFamilierelasjon.PDLRelasjonsRolle minRolle) {

    static enum PDLRelasjonsRolle {
        BARN,
        MOR,
        FAR,
        MEDMOR
    }
}