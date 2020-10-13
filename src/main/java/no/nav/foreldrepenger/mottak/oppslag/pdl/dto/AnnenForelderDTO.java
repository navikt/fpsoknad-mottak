package no.nav.foreldrepenger.mottak.oppslag.pdl.dto;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.Navn;

@Builder
@Data
public class AnnenForelderDTO {
    private final String fnr;
    @JsonIgnore
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
