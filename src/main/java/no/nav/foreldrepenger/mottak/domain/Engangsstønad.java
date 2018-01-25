package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Engangsstønad extends Ytelse {

    private final Medlemsskap medlemsskap;
    private AnnenForelder annenForelder;
    private final RelasjonTilBarn relasjonTilBarn;

    @JsonCreator
    public Engangsstønad(@JsonProperty("medlemsskap") Medlemsskap medlemsskap,
            @JsonProperty("relasjonTilBarn") RelasjonTilBarn relasjonTilBarn) {
        this.medlemsskap = medlemsskap;
        this.relasjonTilBarn = relasjonTilBarn;
    }
}
