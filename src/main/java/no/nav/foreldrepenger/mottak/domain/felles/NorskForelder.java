package no.nav.foreldrepenger.mottak.domain.felles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;

import javax.validation.constraints.NotBlank;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = { "navn" })
public final class NorskForelder extends AnnenForelder {

    @NotBlank
    private final Fødselsnummer fnr;
    private final String navn;

    public NorskForelder(@JsonProperty("fnr") Fødselsnummer fnr) {
        this(fnr, null);
    }

    @JsonCreator
    public NorskForelder(@JsonProperty("fnr") Fødselsnummer fnr, @JsonProperty("navn") String navn) {
        this.fnr = fnr;
        this.navn = navn;
    }
}

