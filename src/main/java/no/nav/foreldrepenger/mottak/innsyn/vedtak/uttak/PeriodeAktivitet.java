package no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak;

import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.StønadskontoType;

public record PeriodeAktivitet(
        String arbeidsforholdId,
        ProsentAndel arbeidstidProsent,
        AvslagsÅrsak avslagsÅrsak,
        Boolean gradering,
        Integer trekkDager,
        StønadskontoType trekkonto,
        ProsentAndel utbetalingProsent,
        ArbeidType arbeidType,
        String virksomhet) {

}
