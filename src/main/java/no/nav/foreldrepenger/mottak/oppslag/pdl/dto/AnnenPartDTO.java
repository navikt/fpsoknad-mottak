package no.nav.foreldrepenger.mottak.oppslag.pdl.dto;

import java.time.LocalDate;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.Navn;

@Builder
@Data
public class AnnenPartDTO {
    private final String fnr;
    private final Navn navn;
    private final LocalDate fødselsdato;

    String getFornavn() {
        return Optional.ofNullable(navn).map(Navn::getFornavn).orElse(null);
    }

    String getMellonavn() {
        return Optional.ofNullable(navn).map(Navn::getMellomnavn).orElse(null);
    }

    String getEtternavn() {
        return Optional.ofNullable(navn).map(Navn::getEtternavn).orElse(null);
    }
}
