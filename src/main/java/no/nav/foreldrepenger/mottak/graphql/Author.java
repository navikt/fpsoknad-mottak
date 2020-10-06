package no.nav.foreldrepenger.mottak.graphql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Author {
    private final String firstName;
    private final String lastName;

    @JsonCreator
    public Author(@JsonProperty("firstname") String firstName, @JsonProperty("lastname") String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
