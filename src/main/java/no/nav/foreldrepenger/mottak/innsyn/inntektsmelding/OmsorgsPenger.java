package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import java.util.List;

import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;

record OmsorgsPenger(boolean harUtbetaltPliktigeDager, List<LukketPeriode> fraværsPerioder,
        List<DelvisFraværsPeriode> delvisFraværsPerioder) {

}
