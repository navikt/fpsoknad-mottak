package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.validation.PastOrToday;
import no.nav.foreldrepenger.mottak.domain.validation.Termindato;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public class FremtidigFødsel extends RelasjonTilBarnMedVedlegg {
    @Termindato
    private final LocalDate terminDato;
    @PastOrToday
    private final LocalDate utstedtDato;

    public FremtidigFødsel(LocalDate terminDato, LocalDate utstedtDato) {
        this(1, terminDato, utstedtDato, Collections.emptyList());
    }

    @JsonCreator
    public FremtidigFødsel(int antallBarn, LocalDate terminDato,
            LocalDate utstedtDato, List<Vedlegg> vedlegg) {
        super(antallBarn, vedlegg == null ? Collections.emptyList() : vedlegg);
        this.terminDato = terminDato;
        this.utstedtDato = utstedtDato;
    }
}
