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
// @JsonSerialize(using = BrukerSerializer.class)
// @JsonDeserialize(using = BrukerDeserializer.class)
public abstract class Bruker {
    private final String value;

    public Bruker(String value) {
        this.value = value;
    }

}
