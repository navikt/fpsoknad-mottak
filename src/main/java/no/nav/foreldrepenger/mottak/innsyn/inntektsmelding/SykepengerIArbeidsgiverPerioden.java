package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import java.util.List;

import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;

record SykepengerIArbeidsgiverPerioden(List<LukketPeriode> arbeidsgiverperiode, double bruttoUtbetalt, String begrunnelse) {

}
