package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class PDLSivilstand {
    private final PDLSivilstand.PDLSivilstandType type;
    private final String relatertVedSivilstand;

    @JsonCreator
    PDLSivilstand(@JsonProperty("type") PDLSivilstand.PDLSivilstandType type,
            @JsonProperty("relatertVedSivilstand") String relatertVedSivilstand) {
        this.type = type;
        this.relatertVedSivilstand = relatertVedSivilstand;
    }

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