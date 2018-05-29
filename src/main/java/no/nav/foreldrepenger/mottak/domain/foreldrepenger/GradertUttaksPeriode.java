package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public class GradertUttaksPeriode extends UttaksPeriode {
    private double arbeidstidProsent;
    private boolean erArbeidstaker;
    private String virksomhetsNummer;
    private boolean arbeidsForholdSomskalGraderes;

    @Builder
    public GradertUttaksPeriode(LocalDate fom, LocalDate tom, List<Vedlegg> vedlegg,
            UttaksperiodeType uttaksperiodeType, boolean ønskerSamtidigUttak, MorsAktivitetstype morsAktivitetsType) {
        super(fom, tom, vedlegg, uttaksperiodeType, ønskerSamtidigUttak, morsAktivitetsType);

    }
}
