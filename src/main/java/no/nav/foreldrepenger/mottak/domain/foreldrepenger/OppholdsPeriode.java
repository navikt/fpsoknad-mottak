package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OppholdsPeriode extends LukketPeriodeMedVedlegg {

    private final Oppholdsårsak årsak;

    public OppholdsPeriode(LocalDate fom, LocalDate tom, List<Vedlegg> vedlegg, Oppholdsårsak årsak) {
        super(fom, tom, vedlegg == null ? Collections.emptyList() : vedlegg);
        this.årsak = årsak;
    }
}
