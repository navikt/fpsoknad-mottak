package no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling;

public enum UtsettelsesÅrsak {
    ARBEID,
    LOVBESTEMT_FERIE,
    SYKDOM,
    FRI,
    INSTITUSJONSOPPHOLD_SØKER,
    INSTITUSJONSOPPHOLD_BARNET,
    HV_OVELSE("periode.utsettelse.hv"),
    NAV_TILTAK("periode.utsettelse.nav");

    private final String key;

    UtsettelsesÅrsak() {
        this(null);
    }

    UtsettelsesÅrsak(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
