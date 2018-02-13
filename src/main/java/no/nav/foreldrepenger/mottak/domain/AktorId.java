package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AktorId {

    private final String id;

    @JsonCreator
    public AktorId(@JsonProperty("id") String id) {
        this.id = id;
    }

    public static AktorId valueOf(String id) {
        return new AktorId(id);
    }
}
