package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.util.StringUtil.partialMask;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class Fødselsnummer {

    @JsonValue
    private final String fnr;

    public Fødselsnummer(String fnr) {
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
