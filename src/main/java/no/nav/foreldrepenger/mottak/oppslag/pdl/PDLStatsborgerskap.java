package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;

@Data class PDLStatsborgerskap {
    private final CountryCode land;

    @JsonCreator
    public PDLStatsborgerskap(@JsonProperty("land") String land) {
        this.land = CountryCode.getByAlpha3Code(land);
    }
}