package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HelTilrettelegging extends Tilrettelegging {

    @NotNull
    private final LocalDate tilrettelagtArbeidFom;

    public HelTilrettelegging(Arbeidsforhold arbeidsforhold, LocalDate behovForTilretteleggingFom,
            LocalDate tilrettelagtArbeidFom, List<String> vedlegg) {
        super(arbeidsforhold, behovForTilretteleggingFom, vedlegg);
        this.tilrettelagtArbeidFom = tilrettelagtArbeidFom;
    }
}
