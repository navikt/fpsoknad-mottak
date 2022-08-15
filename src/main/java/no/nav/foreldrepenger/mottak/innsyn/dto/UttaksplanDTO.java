package no.nav.foreldrepenger.mottak.innsyn.dto;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.common.domain.foreldrepenger.Dekningsgrad;

public record UttaksplanDTO(LocalDate termindato,
                            LocalDate fødselsdato,
                            LocalDate omsorgsovertakelsesdato,
                            Dekningsgrad dekningsgrad,
                            Integer antallBarn,
                            Boolean søkerErFarEllerMedmor,
                            Boolean morErAleneOmOmsorg,
                            Boolean morHarRett,
                            Boolean morErUfør,
                            Boolean annenPartHarRettPåForeldrepengerIEØS,
                            Boolean farMedmorErAleneOmOmsorg,
                            Boolean farMedmorHarRett,
                            Boolean annenForelderErInformert,
                            List<UttaksPeriodeDTO> uttaksPerioder) {
    public UttaksplanDTO {
        uttaksPerioder = Optional.ofNullable(uttaksPerioder).orElse(emptyList());
    }
}
