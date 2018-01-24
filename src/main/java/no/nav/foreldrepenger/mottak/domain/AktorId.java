package no.nav.foreldrepenger.mottak.domain;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class AktorId {
    private final String value;

    public AktorId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        AktorId that = (AktorId) o;
        return Objects.equals(value, that.value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [value=" + value + "]";
    }

}
