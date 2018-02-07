package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AktorId extends Bruker {

    @JsonCreator
    public AktorId(@JsonProperty("value") String value) {
        super(value);
    }

    public static AktorId valueOf(String value) {
        return new AktorId(value);
    }
}
