package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Strings;

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
        return getClass().getSimpleName() + " [fnr=" + mask(fnr) + "]";
    }

    private static String mask(String value) {
        return value != null && value.length() == 11 ? Strings.padEnd(value.substring(0, 6), 11, '*') : value;
    }

}
