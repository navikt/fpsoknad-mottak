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

    public NorskForelder(Fødselsnummer fnr) {
        this(true, null, fnr);
    }

    @JsonCreator
    public NorskForelder(@JsonProperty("lever") boolean lever, @JsonProperty("navn") Navn navn,
            @JsonProperty("fnr") Fødselsnummer fnr) {
        super(lever, navn);
        this.fnr = fnr;
    }

    @Override
    public boolean hasId() {
        return fnr != null && fnr.getFnr() != null;
    }
}
