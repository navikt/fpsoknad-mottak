package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Person extends PersonDetaljer {
    private final String value;

    @JsonCreator
    public Person(String value) {
        Objects.requireNonNull(value,"Fødselsnummer kan ikke være null");
        this.value = value;
    }

    @JsonProperty
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "Fødselsnummer: ************";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person that = (Person) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Person.class.getSimpleName(), value);
    }
}
