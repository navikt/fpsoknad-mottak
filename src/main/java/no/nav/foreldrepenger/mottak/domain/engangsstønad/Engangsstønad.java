package no.nav.foreldrepenger.mottak.domain.engangsstønad;

import javax.validation.Valid;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.Ytelse;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.RelasjonTilBarn;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Engangsstønad extends Ytelse {

    @Valid
    private final Medlemsskap medlemsskap;
    @Valid
    private AnnenForelder annenForelder;
    @Valid
    private final RelasjonTilBarn relasjonTilBarn;

    public Engangsstønad(Medlemsskap medlemsskap, RelasjonTilBarn relasjonTilBarn) {
        this.medlemsskap = medlemsskap;
        this.relasjonTilBarn = relasjonTilBarn;
    }
}
