package no.nav.foreldrepenger.mottak.domain.felles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;

@Data
public class AnnenPart {

    private final Fødselsnummer fnr;
    private final AktorId aktørId;
    private final Navn navn;

    @JsonCreator
    public AnnenPart(@JsonProperty("fnr") Fødselsnummer fnr, @JsonProperty("aktørId") String aktørId,
            @JsonProperty("navn") Navn navn) {
        super();
        this.fnr = fnr;
        this.aktørId = new AktorId(aktørId);
        this.navn = navn;
    }
}
