package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = ValgfrittVedlegg.class, name = "valgfritt"),
        @Type(value = PåkrevdVedlegg.class, name = "påkrevd")
})
public abstract class Vedlegg {

    private final String description;
    private final byte[] vedlegg;

    public Vedlegg(String description, byte[] vedlegg) {
        this.description = description;
        this.vedlegg = vedlegg;
    }
}
