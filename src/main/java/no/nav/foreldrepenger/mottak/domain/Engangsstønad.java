package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Engangsstønad extends Ytelse {

    private final Medlemsskap medlemsskap;
    private AnnenForelder annenForelder;
    private final RelasjonTilBarn relasjon;

    @Builder
    @ConstructorProperties({ "medlemsskap", "relasjon", "annenForelder" })
    public Engangsstønad(Medlemsskap medlemsskap, RelasjonTilBarn relasjon) {
        this.medlemsskap = medlemsskap;
        this.relasjon = relasjon;
    }
}
