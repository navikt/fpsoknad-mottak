package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AktørId extends PersonDetaljer {
    private final String value;

    @JsonCreator
    public AktørId(String value) {
        Objects.requireNonNull(value, "AktørId kan ikke være null");
        this.value = value;
    }

    @JsonProperty
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "AktørId: " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AktørId aktørId = (AktørId) o;
        return Objects.equals(value, aktørId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(AktørId.class.getSimpleName(), value);
    }
}
