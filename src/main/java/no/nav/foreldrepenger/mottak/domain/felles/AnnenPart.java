package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;

@Data
public class AnnenPart {

    private final Fødselsnummer fnr;
    private final AktørId aktørId;
    private final Navn navn;
    private final LocalDate fødselsdato;

    @JsonCreator
    public AnnenPart(@JsonProperty("fnr") Fødselsnummer fnr, @JsonProperty("fødselsdato") LocalDate fødselsdato,
            @JsonProperty("aktørId") AktørId aktørId,
            @JsonProperty("navn") Navn navn) {
        this.fnr = fnr;
        this.aktørId = aktørId;
        this.navn = navn;
        this.fødselsdato = fødselsdato;
    }
}
