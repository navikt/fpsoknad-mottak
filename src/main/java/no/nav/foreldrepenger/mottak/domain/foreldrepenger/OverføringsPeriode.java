package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OverføringsPeriode extends LukketPeriodeMedVedlegg {

    private final Overføringsårsak årsak;

    public OverføringsPeriode(LocalDate fom, LocalDate tom, List<String> vedlegg, Overføringsårsak årsak) {
        super(fom, tom, vedlegg);
        this.årsak = årsak;
    }
}
