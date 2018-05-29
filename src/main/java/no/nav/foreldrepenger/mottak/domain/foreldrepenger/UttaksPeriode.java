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
public class UttaksPeriode extends LukketPeriodeMedVedlegg {
    private final UttaksperiodeType uttaksperiodeType;
    private final boolean ønskerSamtidigUttak;
    private final MorsAktivitetstype morsAktivitetsType;

    public UttaksPeriode(LocalDate fom, LocalDate tom, List<Vedlegg> vedlegg,
            UttaksperiodeType uttaksperiodeType, boolean ønskerSamtidigUttak, MorsAktivitetstype morsAktivitetsType) {
        super(fom, tom, vedlegg != null ? vedlegg : Collections.emptyList());
        this.uttaksperiodeType = uttaksperiodeType;
        this.ønskerSamtidigUttak = ønskerSamtidigUttak;
        this.morsAktivitetsType = morsAktivitetsType;
    }
}
