package no.nav.foreldrepenger.oppslag.domain;

import java.util.Objects;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonValue;

public class Fodselsnummer {

    @Size(min = 11, max = 11)
    private final String fnr;

    public Fodselsnummer(String fnr) {
        this.fnr = Objects.requireNonNull(fnr);
    }

    @JsonValue
    public String getFnr() {
        return fnr;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fnr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        Fodselsnummer that = (Fodselsnummer) o;
        return Objects.equals(fnr, that.fnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + fnr + "]";
    }

}
