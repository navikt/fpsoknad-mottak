package no.nav.foreldrepenger.mottak.oppslag.pdl;

record PDLAdresseBeskyttelse(PDLAdresseBeskyttelse.PDLAdresseGradering gradering) {

    static enum PDLAdresseGradering {
        STRENGT_FORTROLIG_UTLAND,
        STRENGT_FORTROLIG,
        FORTROLIG,
        UGRADERT
    }
}
