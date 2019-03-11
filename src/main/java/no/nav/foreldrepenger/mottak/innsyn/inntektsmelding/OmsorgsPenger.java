package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import java.util.List;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;

@Data
public class OmsorgsPenger {

    private final boolean harUtbetaltPliktigeDager;
    private final List<LukketPeriode> fraværsPerioder;
    private final List<DelvisFraværsPeriode> delvisFraværsPerioder;

}
