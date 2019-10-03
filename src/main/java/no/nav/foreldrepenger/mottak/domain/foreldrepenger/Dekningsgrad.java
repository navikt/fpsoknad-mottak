package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;

public enum Dekningsgrad {

    GRAD80("80"), GRAD100("100");

    private final String kode;

    Dekningsgrad(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static Dekningsgrad create(String value) {
        for (Dekningsgrad val : values()) {
            if (val.name().equals(value) || val.kode.equals(value)) {
                return val;
            }
        }
        throw new UnexpectedInputException("Ikke støttet dekningsgrad %s.", value);
    }

    @JsonValue
    public String kode() {
        return kode;
    }

    public static Dekningsgrad fraKode(String kode) {
        return Arrays.stream(Dekningsgrad.values())
                .filter(e -> e.kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet dekningsgrad %s.", kode));
    }
}
