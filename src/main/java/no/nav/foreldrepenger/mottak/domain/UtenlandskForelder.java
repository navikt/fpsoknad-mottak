package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UtenlandskForelder extends KjentForelder {

    private final CountryCode land;

    @JsonCreator
    public UtenlandskForelder(@JsonProperty("lever") boolean lever, @JsonProperty("land") CountryCode land) {
        super(lever);
        this.land = land;
    }

}
