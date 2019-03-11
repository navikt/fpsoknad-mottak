package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SelvstendigNæringsdrivende extends Arbeidsforhold {
    private final String risikoFaktorer;
    private final String tilretteleggingstiltak;
}
