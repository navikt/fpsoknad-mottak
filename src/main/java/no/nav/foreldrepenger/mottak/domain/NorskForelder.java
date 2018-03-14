package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public final class NorskForelder extends KjentForelder {

    private final Fødselsnummer fnr;

    @JsonCreator
    public NorskForelder(@JsonProperty("lever") boolean lever, @JsonProperty("fnr") Fødselsnummer fnr) {
        super(lever);
        this.fnr = fnr;
    }

}
