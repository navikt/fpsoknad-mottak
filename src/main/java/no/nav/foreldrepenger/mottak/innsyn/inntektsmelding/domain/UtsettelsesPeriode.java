package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.domain;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;

@Data
public class UtsettelsesPeriode {
    private final LukketPeriode periode;
    private final UtsettelsesÅrsak utsettelsesÅrsak;

}
