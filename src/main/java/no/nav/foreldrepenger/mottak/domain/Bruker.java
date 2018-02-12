package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = AktorId.class, name = "aktør"),
        @Type(value = Fodselsnummer.class, name = "fnr")
})
public abstract class Bruker {
    private final String id;
    private final Navn navn;

    public Bruker(String id) {
        this(id, null);
    }

    public Bruker(String id, Navn navn) {
        this.id = id;
        this.navn = navn;
    }
}
