package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IngenTilrettelegging extends Tilrettelegging {

    private final LocalDate slutteArbeidFom;

    public IngenTilrettelegging(Arbeidsforhold arbeidsforhold, LocalDate slutteArbeidFom, List<String> vedlegg) {
        super(arbeidsforhold, null, vedlegg);
        this.slutteArbeidFom = slutteArbeidFom;
    }
}
