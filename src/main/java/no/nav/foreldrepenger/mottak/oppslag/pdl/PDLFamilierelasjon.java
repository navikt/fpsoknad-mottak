package no.nav.foreldrepenger.mottak.oppslag.pdl;

record PDLFamilierelasjon(String id,
        PDLFamilierelasjon.PDLRelasjonsRolle relatertPersonsrolle,
        PDLFamilierelasjon.PDLRelasjonsRolle minRolle) {

    static enum PDLRelasjonsRolle {
        BARN,
        MOR,
        FAR,
        MEDMOR
    }
}