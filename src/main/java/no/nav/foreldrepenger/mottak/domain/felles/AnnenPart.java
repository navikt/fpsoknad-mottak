package no.nav.foreldrepenger.mottak.domain.felles;

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

    @JsonCreator
    public AnnenPart(@JsonProperty("fnr") Fødselsnummer fnr, @JsonProperty("aktørId") AktørId aktørId,
            @JsonProperty("navn") Navn navn) {
        super();
        this.fnr = fnr;
        this.aktørId = aktørId;
        this.navn = navn;
    }
}
