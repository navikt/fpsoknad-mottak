package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Fodselsnummer {

    private final String id;

    @JsonCreator
    public Fodselsnummer(@JsonProperty("id") String id) {
        this.id = id;
    }

    public static Fodselsnummer valueOf(String id) {
        return new Fodselsnummer(id);
    }

}
