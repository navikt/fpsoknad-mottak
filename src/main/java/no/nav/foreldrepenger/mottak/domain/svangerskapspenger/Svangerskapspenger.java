package no.nav.foreldrepenger.mottak.domain.svangerskapspenger;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.foreldrepenger.mottak.domain.Ytelse;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging;

import javax.validation.Valid;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class Svangerskapspenger extends Ytelse {

    @Valid
    private final Medlemsskap medlemsskap;
    @Valid
    private final Opptjening opptjening;

    private final List<Tilrettelegging> tilrettelegging;
}
