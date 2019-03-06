package no.nav.foreldrepenger.mottak.domain.felles.medlemskap;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;

@Data
public class Utenlandsopphold {

    @NotNull
    private final CountryCode land;
    private final LukketPeriode varighet;

    @JsonCreator
    public Utenlandsopphold(@JsonProperty("land") CountryCode land, @JsonProperty("varighet") LukketPeriode varighet) {
        this.land = land;
        this.varighet = varighet;
    }
}
