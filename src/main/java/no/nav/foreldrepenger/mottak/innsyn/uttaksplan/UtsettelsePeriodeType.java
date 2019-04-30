package no.nav.foreldrepenger.mottak.innsyn.uttaksplan;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UtsettelsePeriodeType {
    ARBEID,
    LOVBESTEMT_FERIE,
    @JsonProperty("LOVBESTEMT_FERIE")
    FERIE,
    SYKDOM_SKADE,
    SØKER_INNLAGT,
    BARN_INNLAGT
}
