package no.nav.foreldrepenger.mottak.oppslag.pdl;

record PDLSivilstand(PDLSivilstand.Type type) {

    public enum Type {
        UOPPGITT,
        UGIFT,
        GIFT,
        ENKE_ELLER_ENKEMANN,
        SKILT,
        SEPARERT,
        REGISTRERT_PARTNER,
        SEPARERT_PARTNER,
        SKILT_PARTNER,
        GJENLEVENDE_PARTNER,
    }
}
