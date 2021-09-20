package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;


record GraderingsPeriode(ProsentAndel prosent, LukketPeriode periode) {
}
