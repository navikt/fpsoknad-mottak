package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class AktørId {

    @JsonValue
    private final String id;

    public AktørId(String id) {
        this.id = id;
    }

    public static AktørId valueOf(String id) {
        return new AktørId(id);
    }
}
