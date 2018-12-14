package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = ArbeidsgiverVirksomhet.class, name = "virksomhet"),
        @Type(value = ArbeidsgiverPerson.class, name = "person")
})
public abstract class Arbeidsgiver {
    private final String id;

    @JsonCreator
    public Arbeidsgiver(String id) {
        this.id = id;
    }
}
