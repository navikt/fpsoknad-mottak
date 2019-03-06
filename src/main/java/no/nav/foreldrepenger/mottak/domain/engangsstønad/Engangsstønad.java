package no.nav.foreldrepenger.mottak.domain.engangsstønad;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.Ytelse;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.RelasjonTilBarn;

import javax.validation.Valid;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonPropertyOrder({ "medlemsskap", "relasjonTilBarn", "annenForelder" })

public class Engangsstønad extends Ytelse {

    @Valid
    private final Medlemsskap medlemsskap;
    @Valid
    private AnnenForelder annenForelder;
    @Valid
    private final RelasjonTilBarn relasjonTilBarn;

    @JsonCreator
    public Engangsstønad(@JsonProperty("medlemsskap") Medlemsskap medlemsskap,
            @JsonProperty("relasjonTilBarn") RelasjonTilBarn relasjonTilBarn) {
        this.medlemsskap = medlemsskap;
        this.relasjonTilBarn = relasjonTilBarn;
    }
}
