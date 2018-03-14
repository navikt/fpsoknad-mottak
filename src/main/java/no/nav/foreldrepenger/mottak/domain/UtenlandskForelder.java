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
    private final String id;

    public UtenlandskForelder(CountryCode land) {
        this(true, null, null, land);
    }

    @JsonCreator
    public UtenlandskForelder(@JsonProperty("lever") boolean lever, @JsonProperty("navn") Navn navn,
            @JsonProperty("id") String id,
            @JsonProperty("land") CountryCode land) {
        super(lever, navn);
        this.id = id;
        this.land = land;
    }
}
