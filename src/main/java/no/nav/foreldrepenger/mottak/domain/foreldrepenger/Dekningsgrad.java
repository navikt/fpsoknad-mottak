package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;

public enum Dekningsgrad {

    GRAD80(80),
    GRAD100(100);

    private final int kode;

    Dekningsgrad(int kode) {
        this.kode = kode;
    }

    @JsonCreator(mode = Mode.DELEGATING)
    public static Dekningsgrad create(JsonNode node) {
        return node.isTextual() ? create(node.asText()) : create(node.asInt());
    }

    private static Dekningsgrad create(int value) {
        return Arrays.stream(values())
                .filter(v -> v.kode == value)
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet dekningsgrad %s.", value));
    }

    public static Dekningsgrad create(String value) {
        return Arrays.stream(values())
                .filter(v -> v.name().equals(value) || String.valueOf(v.kode).equals(value))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet dekningsgrad %s.", value));
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
