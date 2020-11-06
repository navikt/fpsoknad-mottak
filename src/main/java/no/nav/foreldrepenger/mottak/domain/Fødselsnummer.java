package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.util.StringUtil.partialMask;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class Fødselsnummer {

    @JsonValue
    private final String fnr;

    @JsonCreator
    public Fødselsnummer(@JsonProperty("fnr") String fnr) {
        this.fnr = fnr;
    }

    public static Fødselsnummer valueOf(String fnr) {
        return new Fødselsnummer(fnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + partialMask(fnr) + "]";
    }
}
