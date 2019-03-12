package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;

@Data
@ToString(callSuper = true)
public class DelvisTilrettelegging extends Tilrettelegging {

    private final LocalDate tilrettelagtArbeidFom;
    private final ProsentAndel stillingsprosent;

    public DelvisTilrettelegging(Arbeidsforhold arbeidsforhold, LocalDate behovForTilretteleggingFom,
            LocalDate tilrettelagtArbeidFom,
            ProsentAndel stillingsprosent, List<String> vedlegg) {
        super(arbeidsforhold, behovForTilretteleggingFom, vedlegg);
        this.tilrettelagtArbeidFom = tilrettelagtArbeidFom;
        this.stillingsprosent = stillingsprosent;
    }
}
