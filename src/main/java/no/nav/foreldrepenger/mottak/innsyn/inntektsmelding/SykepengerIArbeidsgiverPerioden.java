package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import java.util.List;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;

@Data
public class SykepengerIArbeidsgiverPerioden {
    private final List<LukketPeriode> arbeidsgiverperiode;
    private final double bruttoUtbetalt;
    private final String begrunnelse;

}
