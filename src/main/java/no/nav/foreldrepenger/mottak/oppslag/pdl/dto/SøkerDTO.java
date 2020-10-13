package no.nav.foreldrepenger.mottak.oppslag.pdl.dto;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;

@Data
@Builder
public class SøkerDTO {

    private final String id;
    private final CountryCode landKode;
    private final Kjønn kjønn;
    private final LocalDate fødselsdato;
    private final String målform;
    private final Bankkonto bankkonto;
    @JsonUnwrapped
    private final Navn navn;
    private final Set<BarnDTO> barn;
}
