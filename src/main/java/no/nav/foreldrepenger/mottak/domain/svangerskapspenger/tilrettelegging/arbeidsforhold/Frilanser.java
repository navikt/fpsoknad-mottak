package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Frilanser extends Arbeidsforhold {
    @NotNull
    private final String risikoFaktorer;
    @NotNull
    private final String tilretteleggingstiltak;
}
