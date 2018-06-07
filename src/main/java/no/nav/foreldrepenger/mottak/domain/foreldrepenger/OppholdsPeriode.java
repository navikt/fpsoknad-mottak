package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OppholdsPeriode extends LukketPeriodeMedVedlegg {

    private final Oppholdsårsak årsak;

    public OppholdsPeriode(LocalDate fom, LocalDate tom, List<String> vedlegg, Oppholdsårsak årsak) {
        super(fom, tom, vedlegg);
        this.årsak = årsak;
    }
}
