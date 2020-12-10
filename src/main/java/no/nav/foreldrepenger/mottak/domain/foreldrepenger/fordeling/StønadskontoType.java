package no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum StønadskontoType {
    @JsonEnumDefaultValue
    @JsonProperty("-")
    IKKE_SATT,
    FELLESPERIODE,
    MØDREKVOTE,
    FEDREKVOTE,
    FORELDREPENGER,
    FORELDREPENGER_FØR_FØDSEL;

    private static final Logger LOG = LoggerFactory.getLogger(StønadskontoType.class);

    public static StønadskontoType valueSafelyOf(String name) {
        try {
            return StønadskontoType.valueOf(name);
        } catch (Exception e) {
            LOG.warn("Ingen enum verdi for {}", name);
            return null;
        }
    }
}
