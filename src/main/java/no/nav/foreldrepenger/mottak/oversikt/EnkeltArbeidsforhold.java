package no.nav.foreldrepenger.mottak.oversikt;

import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;

import java.time.LocalDate;
import java.util.Optional;

public record EnkeltArbeidsforhold(String arbeidsgiverId,
                                   String arbeidsgiverIdType,
                                   LocalDate from,
                                   Optional<LocalDate> to,
                                   ProsentAndel stillingsprosent,
                                   String arbeidsgiverNavn) {
}
