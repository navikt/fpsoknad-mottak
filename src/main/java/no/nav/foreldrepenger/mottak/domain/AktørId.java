package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class AktørId {

    @JsonValue
    private final String id;

    @JsonCreator
    public AktørId(@JsonProperty("id") String id) {
        this.id = id;
    }

    public static AktørId valueOf(String id) {
        return new AktørId(id);
    }
}
