package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;


record GraderingsPeriode(ProsentAndel prosent, LukketPeriode periode) {
}
