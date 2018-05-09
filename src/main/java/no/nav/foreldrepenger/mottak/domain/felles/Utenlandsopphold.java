package no.nav.foreldrepenger.mottak.domain.felles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;

@Data
public class Utenlandsopphold {

    private final CountryCode land;
    private final LukketPeriode varighet;

    @JsonCreator
    public Utenlandsopphold(@JsonProperty("land") CountryCode land, @JsonProperty("varighet") LukketPeriode varighet) {
        this.land = land;
        this.varighet = varighet;
    }
}
