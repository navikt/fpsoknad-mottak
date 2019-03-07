package no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StønadskontoType {
    @JsonProperty("-")
    IKKE_SATT, FELLESPERIODE, MØDREKVOTE, FEDREKVOTE, FORELDREPENGER, FORELDREPENGER_FØR_FØDSEL
}
