package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class PDLStatsborgerskap {
    private final String land;

    @JsonCreator
    public PDLStatsborgerskap(@JsonProperty("land") String land) {
        this.land = land;
    }
}