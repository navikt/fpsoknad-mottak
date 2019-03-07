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
public class HelTilrettelegging extends Tilrettelegging {

    private final LocalDate tilrettelagtArbeidFom;

    public HelTilrettelegging(Arbeidsforhold arbeidsforhold, LocalDate behovForTilretteleggingFom, List<String> vedlegg, LocalDate tilrettelagtArbeidFom) {
        super(arbeidsforhold, behovForTilretteleggingFom, vedlegg);
        this.tilrettelagtArbeidFom = tilrettelagtArbeidFom;
    }
}
