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
    private final StønadskontoType uttaksperiodeType;

    public OverføringsPeriode(LocalDate fom, LocalDate tom, Overføringsårsak årsak,
            StønadskontoType uttaksperiodeType,
            List<String> vedlegg) {
        super(fom, tom, vedlegg);
        this.årsak = årsak;
        this.uttaksperiodeType = uttaksperiodeType;
    }
}
