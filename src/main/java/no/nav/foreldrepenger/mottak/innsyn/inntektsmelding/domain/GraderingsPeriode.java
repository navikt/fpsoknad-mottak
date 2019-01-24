package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.domain;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;

@Data
public class GraderingsPeriode {
    private final ProsentAndel prosent;
    private final LukketPeriode periode;

}
