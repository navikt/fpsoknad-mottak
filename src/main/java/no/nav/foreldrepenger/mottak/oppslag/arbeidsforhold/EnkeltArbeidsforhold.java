package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.time.LocalDate;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;

@Data
@Builder
public class EnkeltArbeidsforhold {
    private final String arbeidsgiverId;
    private final String arbeidsgiverIdType;
    private final LocalDate from;
    private final Optional<LocalDate> to;
    private final ProsentAndel stillingsprosent;
    private String arbeidsgiverNavn;
}
