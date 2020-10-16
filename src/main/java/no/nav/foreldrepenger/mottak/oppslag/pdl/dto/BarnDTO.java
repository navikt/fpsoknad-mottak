package no.nav.foreldrepenger.mottak.oppslag.pdl.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenPart;

@Builder
@Data
public class BarnDTO {
    private final Fødselsnummer fnr;
    private final Fødselsnummer fnrSøker;
    private final LocalDate fødselsdato;
    private final Navn navn;
    private final AnnenPart annenPart;
}
