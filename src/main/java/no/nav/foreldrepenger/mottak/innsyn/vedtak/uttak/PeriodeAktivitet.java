package no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak;

import lombok.Data;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;

@Data
public class PeriodeAktivitet {

    private final String arbeidsforholdId;
    private final ProsentAndel arbeidstidProsent;
    private final AvslagsÅrsak avslagsÅrsak;
    private final Boolean gradering;
    private final Integer trekkDager;
    private final StønadskontoType trekkonto;
    private final ProsentAndel utbetalingProsent;
    private final ArbeidType arbeidType;
    private final String virksomhet;

}
