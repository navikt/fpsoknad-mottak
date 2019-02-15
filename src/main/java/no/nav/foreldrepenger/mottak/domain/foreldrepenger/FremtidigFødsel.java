package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.PastOrToday;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Termindato;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public class FremtidigFødsel extends RelasjonTilBarnMedVedlegg {
    @Termindato
    private final LocalDate terminDato;
    @PastOrToday(nullable = true)
    private final LocalDate utstedtDato;

    public FremtidigFødsel(LocalDate terminDato, LocalDate utstedtDato) {
        this(1, terminDato, utstedtDato, emptyList());
    }

    @JsonCreator
    public FremtidigFødsel(int antallBarn, LocalDate terminDato,
            LocalDate utstedtDato, List<String> vedlegg) {
        super(antallBarn, vedlegg);
        this.terminDato = terminDato;
        this.utstedtDato = utstedtDato;
    }

    @Override
    public LocalDate relasjonsDato() {
        return terminDato;
    }
}
