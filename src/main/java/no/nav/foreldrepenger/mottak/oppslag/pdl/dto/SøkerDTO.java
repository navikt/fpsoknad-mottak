package no.nav.foreldrepenger.mottak.oppslag.pdl.dto;

import java.time.LocalDate;
import java.util.Set;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;

@Data
@Builder
@EqualsAndHashCode(exclude = "aktørId") // TODO midlertidig
public class SøkerDTO {

    private final Fødselsnummer id;
    private final AktørId aktørId;
    private final CountryCode landKode;
    private final Kjønn kjønn;
    private final LocalDate fødselsdato;
    private final String målform;
    private final Bankkonto bankkonto;
    private final Navn navn;
    private final Set<BarnDTO> barn;
}
