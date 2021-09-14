package no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak;

import java.util.List;

import lombok.Data;
import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;

@Data
public class UttaksPeriode {

    private final LukketPeriode periode;
    private final UttaksPeriodeResultatType resultatType;
    private final UttaksPeriodeResultatÅrsak årsak;
    private final String begrunnelse;
    private final List<PeriodeAktivitet> periodeAktiviteter;
    private final Boolean graderingInnvilget;
    private final Boolean samtidigUttak;
    private final Boolean manueltBehandlet;
    private final ManuellBehandlingsÅrsak manuellBehandlingsårsak;
}
