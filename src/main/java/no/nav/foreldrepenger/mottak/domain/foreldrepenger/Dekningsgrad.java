package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;

public enum Dekningsgrad {

    GRAD80(80),
    GRAD100(100);

    private final int kode;

    Dekningsgrad(int kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static Dekningsgrad create(int value) {
        for (Dekningsgrad val : values()) {
            if (val.kode == value) {
                return val;
            }
        }
        throw new UnexpectedInputException("Ikke støttet dekningsgrad %s.", value);
    }

    @JsonValue
    public int kode() {
        return kode;
    }

    public static Dekningsgrad fraKode(String kode) {
        return fraKode(Integer.valueOf(kode));
    }

    public static Dekningsgrad fraKode(int kode) {
        return Arrays.stream(Dekningsgrad.values())
                .filter(e -> e.kode == kode)
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet dekningsgrad %s.", kode));
    }
}
