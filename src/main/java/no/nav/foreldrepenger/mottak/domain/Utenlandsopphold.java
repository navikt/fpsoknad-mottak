package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;

@Data
public class Utenlandsopphold {

    private final CountryCode land;
    private final LukketPeriode varighet;

    public Utenlandsopphold(CountryCode land) {
        this(land, new LukketPeriode());
    }

    @JsonCreator
    public Utenlandsopphold(@JsonProperty("land") CountryCode land, @JsonProperty("varighet") LukketPeriode varighet) {
        this.land = land;
        this.varighet = varighet;
    }
}
