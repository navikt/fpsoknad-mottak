package no.nav.foreldrepenger.mottak.oppslag.pdl;

record PDLSivilstand(PDLSivilstand.PDLSivilstandType type, String relatertVedSivilstand) {

    static enum PDLSivilstandType {
        UOPPGITT,
        UGIFT,
        GIFT,
        ENKE_ELLER_ENKEMANN,
        SKILT,
        SEPARERT,
        REGISTRERT_PARTNER,
        SEPARERT_PARTNER,
        SKILT_PARTNER,
        GJENLEVENDE_PARTNER
    }
}