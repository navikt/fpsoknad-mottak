package no.nav.foreldrepenger.mottak.oppslag.pdl.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AnnenForelderDTO {
    private final String fnr;
    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
    private final LocalDate fødselsdato;
}
