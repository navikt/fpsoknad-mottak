package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IngenTilrettelegging extends Tilrettelegging {

    private final LocalDate slutteArbeidFom;

    public IngenTilrettelegging(Arbeidsforhold arbeidsforhold, LocalDate behovForTilretteleggingFom, List<String> vedlegg, LocalDate slutteArbeidFom) {
        super(arbeidsforhold, behovForTilretteleggingFom, vedlegg);
        this.slutteArbeidFom = slutteArbeidFom;
    }
}
