package no.nav.foreldrepenger.mottak.innsyn.uttaksplan;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UtsettelsePeriodeType {
    ARBEID,
    FERIE("LOVBESTEMT_FERIE"),
    SYKDOM_SKADE,
    SØKER_INNLAGT,
    BARN_INNLAGT;

    private final String value;

    UtsettelsePeriodeType() {
        this(null);
    }

    UtsettelsePeriodeType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return Optional.ofNullable(value)
                .orElse(name());
    }
}
