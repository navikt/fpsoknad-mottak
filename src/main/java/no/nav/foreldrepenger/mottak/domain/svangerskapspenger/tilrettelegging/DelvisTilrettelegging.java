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
public class DelvisTilrettelegging extends Tilrettelegging {

    private final LocalDate tilrettelagtArbeidFom;
    private final Double stillingsprosent;

    public DelvisTilrettelegging(Arbeidsforhold arbeidsforhold, LocalDate behovForTilretteleggingFom, List<String> vedlegg,
                                 LocalDate tilrettelagtArbeidFom, Double stillingsprosent) {
        super(arbeidsforhold, behovForTilretteleggingFom, vedlegg);
        this.tilrettelagtArbeidFom = tilrettelagtArbeidFom;
        this.stillingsprosent = stillingsprosent;
    }
}
